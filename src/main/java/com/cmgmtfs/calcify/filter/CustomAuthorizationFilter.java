package com.cmgmtfs.calcify.filter;

import com.cmgmtfs.calcify.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Map.of;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String[] PUBLIC_ROUTES = {"/user/login", "/user/verify/code", "/user/register"};
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    private final TokenProvider tokenProvider; // dependency injection
    protected static final String TOKEN_KEY = "token";
    protected static final String EMAIL_KEY = "email";

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // we want to do this in a clean way
            // usually there's too many if statements here

            // shouldNotFilter() method returns false
            // we can make this cleaner by overriding the shouldNotFilter method from the
            // OncePerRequestFilter parent

            // this allows us to move all the conditionals to the shouldNotFilter method


            Map<String, String> values = getRequestValues(request);

            // look at the token and validate it

            // first get the token
            String token = getToken(request);

            if (tokenProvider.isTokenValid(values.get(EMAIL_KEY), token)) {
                // get all the granted authorities and authentication
                // set all the info on the security context
                List<GrantedAuthority> authorities = tokenProvider.getAuthorities(values.get(TOKEN_KEY));
                Authentication auth = tokenProvider.getAuthentication(values.get(EMAIL_KEY), authorities, request);

                SecurityContextHolder.getContext()
                        .setAuthentication(auth);

            } else {
                // else token not valid
                // then we want to clear the context of this thread
                // because every time there's a new request
                // that comes in the application will create
                // a new thread to process the request

                SecurityContextHolder.clearContext();
            }

            // now let the request continue its course
            // use the filter chain
            // no matter what we do here
            // we always must let the request go
            // because there are other filters that needs
            // to do other things for the framework itself

            // let the request continue to the next filter and the chain
            // VERY IMPORTANT
            filterChain.doFilter(request, response);
            // we don't want to stop the application flow
            // and the way that the filters are being executed

            // the filters are actually going through a loop
            // they are not calling each other (this is the framework side of things)


        } catch (Exception e) {
            log.error(e.getMessage(), e);
//            processError(request, response, e);
        }


    }

    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, EMPTY))
                .get();
    }

    private Map<String, String> getRequestValues(HttpServletRequest request) {
        return of(EMAIL_KEY, tokenProvider.getSubject(getToken(request), request), TOKEN_KEY, getToken(request));
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getHeader(AUTHORIZATION) == null ||
                request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
                request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD) ||
                asList(PUBLIC_ROUTES).contains(request.getRequestURI());
    }
}

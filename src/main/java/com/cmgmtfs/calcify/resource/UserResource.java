package com.cmgmtfs.calcify.resource;

import com.cmgmtfs.calcify.domain.HttpResponse;
import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.domain.UserPrincipal;
import com.cmgmtfs.calcify.dto.UserDTO;
import com.cmgmtfs.calcify.form.LoginForm;
import com.cmgmtfs.calcify.provider.TokenProvider;
import com.cmgmtfs.calcify.service.RoleService;
import com.cmgmtfs.calcify.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.cmgmtfs.calcify.dtomapper.UserDTOMapper.toUser;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RoleService roleService;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        //
        // inject AuthenticationManager
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(),
                loginForm.getPassword()));

        UserDTO userDTO = userService.getUserByEmail(loginForm.getEmail());

        return userDTO.isUsingMfa() ? sendVerificationCode(userDTO) : sendResponse(userDTO);


    }


    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
        UserDTO userDto = userService.createUser(user);
        return ResponseEntity.created(getUri())
                .body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(of("user", userDto))
                                .message("User created")
                                .status(CREATED)
                                .statusCode(CREATED.value())
                                .build());

    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserDTO userDTO = userService.verifyCode(email, code);
        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
//                        .data(of("user", userDTO))
                        .data(of("user",
                                userDTO,
                                "access_token",
                                tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token",
                                tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());

    }

    private URI getUri() {
        return URI.create(fromCurrentContextPath()
                .path("/user/get/<userId>")
                .toUriString());
    }


    private ResponseEntity<HttpResponse> sendResponse(UserDTO userDTO) {
        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
//                        .data(of("user", userDTO))
                        .data(of("user",
                                userDTO,
                                "access_token",
                                tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token",
                                tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private UserPrincipal getUserPrincipal(UserDTO userDTO) {
        return new UserPrincipal(toUser(userService.getUserByEmail(userDTO.getEmail())),
                roleService.getRoleByUserId(userDTO.getId())
                        .getPermission());
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO userDTO) {
        userService.sendVerificationCode(userDTO);
        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO))
                        .message("Verification Code Sent")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
}

package com.cmgmtfs.calcify.resource;

import com.cmgmtfs.calcify.domain.HttpResponse;
import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.dto.UserDTO;
import com.cmgmtfs.calcify.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
        UserDTO userDto = userService.createUser(user);
        return ResponseEntity.created(getUri())
                .body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(of("user", userDto))
                                .message("User created")
                                .status(HttpStatus.CREATED)
                                .statusCode(HttpStatus.CREATED.value())
                                .build());

    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user/get/<userId>")
                .toUriString());
    }
}

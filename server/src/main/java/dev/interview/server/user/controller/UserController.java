package dev.interview.server.user.controller;

import dev.interview.server.user.domain.User;
import dev.interview.server.user.dto.UserResponse;
import dev.interview.server.user.dto.UserSignupRequest;
import dev.interview.server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {
    private final UserService userService;

    // 회원가입 API
    @PostMapping
    @Operation(summary = "회원가입", description = "사용자 새로 등록합니다.")
    public ResponseEntity<UserResponse> signup(@RequestBody UserSignupRequest request) {
        User user = userService.save(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(user));
    }
}

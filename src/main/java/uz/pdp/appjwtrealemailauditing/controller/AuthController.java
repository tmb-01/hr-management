package uz.pdp.appjwtrealemailauditing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjwtrealemailauditing.payload.ApiResponse;
import uz.pdp.appjwtrealemailauditing.payload.LoginDto;
import uz.pdp.appjwtrealemailauditing.payload.RegisterDto;
import uz.pdp.appjwtrealemailauditing.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    private HttpEntity<?> register(@RequestBody RegisterDto registerDto) {
        ApiResponse response = authService.register(registerDto);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDto loginDto) {
        ApiResponse login = authService.login(loginDto);
        return ResponseEntity.status(login.isSuccess() ? 200 : 401).body(login);
    }

    @PostMapping("/verify")
    public HttpEntity<?> verifyEmail(@RequestParam String emailCode, @RequestParam String email, @RequestBody LoginDto loginDto) {
        ApiResponse response = authService.verifyEmail(emailCode, email, loginDto);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.ALREADY_REPORTED).body(response);
    }
}

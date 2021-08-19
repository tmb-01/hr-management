package uz.pdp.appjwtrealemailauditing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.appjwtrealemailauditing.payload.ApiResponse;
import uz.pdp.appjwtrealemailauditing.service.TurnstileService;

@RestController
@RequestMapping("/api/turnstile")
public class TurnstileController {

    @Autowired
    TurnstileService turnstileService;

    @PostMapping
    public HttpEntity<?> enterToWork() {
        ApiResponse response = turnstileService.enterToWork();
        return ResponseEntity.status(response.isSuccess() ? 200 : 401).body(response);
    }

    @PutMapping
    public HttpEntity<?> exitFromWork(){
        ApiResponse response = turnstileService.exitFromWork();
        return ResponseEntity.status(response.isSuccess() ? 200 : 401).body(response);
    }


}

package uz.pdp.appjwtrealemailauditing.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtrealemailauditing.entity.Turnstile;
import uz.pdp.appjwtrealemailauditing.entity.User;
import uz.pdp.appjwtrealemailauditing.payload.ApiResponse;
import uz.pdp.appjwtrealemailauditing.repository.TurnstileRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TurnstileService {

    TurnstileRepository turnstileRepository;

    public TurnstileService(TurnstileRepository turnstileRepository) {
        this.turnstileRepository = turnstileRepository;
    }


    public ApiResponse enterToWork() {
        Turnstile turnstile = new Turnstile();
        turnstile.setStatus(true);
        turnstile.setCameAt(LocalDateTime.now());

        turnstileRepository.save(turnstile);
        return new ApiResponse("Success! You entered!", true);
    }


    public ApiResponse exitFromWork() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {

            User user = (User) authentication.getPrincipal();


            Optional<Turnstile> optionalTurniket = turnstileRepository.findByUserAndStatus(user.getId(), true);
            if (!optionalTurniket.isPresent())
                return new ApiResponse("Such turnstile id not found!", false);

            optionalTurniket.get().setStatus(false);
            optionalTurniket.get().setLeftAt(LocalDateTime.now());

            turnstileRepository.save(optionalTurniket.get());

            return new ApiResponse("Success! You exited!", true);
        }

        return new ApiResponse("Authentication empty!", false);

    }
}

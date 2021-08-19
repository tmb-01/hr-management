package uz.pdp.appjwtrealemailauditing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtrealemailauditing.entity.Role;
import uz.pdp.appjwtrealemailauditing.entity.User;
import uz.pdp.appjwtrealemailauditing.payload.ApiResponse;
import uz.pdp.appjwtrealemailauditing.payload.LoginDto;
import uz.pdp.appjwtrealemailauditing.payload.RegisterDto;
import uz.pdp.appjwtrealemailauditing.repository.RoleRepository;
import uz.pdp.appjwtrealemailauditing.repository.UserRepository;
import uz.pdp.appjwtrealemailauditing.security.JwtProvider;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;

    public ApiResponse register(RegisterDto registerDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal().equals("anonymousUser")) {
            User user = saveTempUserData(registerDto);
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            userRepository.save(user);
            return new ApiResponse("Director added!", true);
        } else {
            User userContext = (User) authentication.getPrincipal();
            Set<Role> roles = userContext.getRoles();

            int role_index = 0;

            for (Role role : roles) {
                // DIRECTOR bo'lsa MANGER qo'shadi
                if (role.getName().name().equals("DIRECTOR")) {
                    role_index = 1;
                }

                // HR_MANAGER bo'lsa boshqa xodimlarni qo’shadi
                if (role.getName().name().equals("HR_MANAGER")) {
                    role_index = 2;
                }
            }

            User user1 = saveTempUserData(registerDto);

            if (role_index == 2) {

                Set<Role> sentRoles = user1.getRoles();
                int role_index2 = 0;
                for (Role sentRole : sentRoles) {
                    // HR_MANAGER -> DIRECTOR, HR_MANAGER va MANAGER qo'sha olmasligi tekshirildi
                    if (sentRole.getName().name().equals("ROLE_DIRECTOR")
                            || sentRole.getName().name().equals("ROLE_HR_MANAGER")
                            || sentRole.getName().name().equals("ROLE_MANAGER"))
                        role_index2 = 1;


                    // HR_MANAGER -> EMPLOYEE qo'sha olishi tekshirildi
                    if (sentRole.getName().name().equals("ROLE_WORKER"))
                        role_index2 = 2;
                }

                if (role_index2 == 1)
                    return new ApiResponse("HR MANAGER can add just EMPLOYEE's", false);

            }
            user1.setPassword("");
            User savedUser = userRepository.save(user1);

            //Managerlarni qo’shganda ularning email manziliga link jo’natiladi
            sendEmail(savedUser.getEmail(), savedUser.getEmailCode());


            return new ApiResponse("Mail sent!", true);
        }
    }

    public User saveTempUserData(RegisterDto registerDto) {
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());

        Set<Role> roles = registerDto.getRoles();
        user.setRoles(roles);
        user.setEmailCode(UUID.randomUUID().toString());
        return user;
    }

    public void sendEmail(String sendingEmail, String emailCode) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("maraimtuxtasunov@gmail.com");
            mailMessage.setTo(sendingEmail);
            mailMessage.setSubject("Account confirmation!");
            mailMessage.setText("<a href='http://localhost:8080/api/auth/verify?emailCode=" + emailCode + "&email=" + sendingEmail + "'>Confirm</a>");

            javaMailSender.send(mailMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public ApiResponse login(LoginDto loginDto) {

        try {
            Authentication authenticate =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            User user = (User) authenticate.getPrincipal();
            String token = jwtProvider.generateToken(user.getUsername(), user.getRoles());
            return new ApiResponse(token, true);
        } catch (BadCredentialsException badCredentialsException) {
            return new ApiResponse("Username or password failed!", false);
        }
    }

    public ApiResponse verifyEmail(String emailCode, String email, LoginDto loginDto) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isPresent()) {
            optionalUser.get().setEnabled(true);
            optionalUser.get().setEmailCode(null);
            optionalUser.get().setPassword(passwordEncoder.encode(loginDto.getPassword()));
            userRepository.save(optionalUser.get());
            return new ApiResponse("Account confirmed!", true);
        }
        return new ApiResponse("Account is already confirmed!", false);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }
}

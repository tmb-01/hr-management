package uz.pdp.appjwtrealemailauditing.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDto {
    @NotNull
    @Email
    private String username;

    @NotNull
    private String password;
}

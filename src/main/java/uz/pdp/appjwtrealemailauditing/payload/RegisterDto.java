package uz.pdp.appjwtrealemailauditing.payload;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import uz.pdp.appjwtrealemailauditing.entity.Role;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class RegisterDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 50)
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Set<Role> roles;
}

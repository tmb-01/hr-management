package uz.pdp.appjwtrealemailauditing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Turnstile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    private UUID user;

    @NotNull
    private LocalDateTime cameAt;

    private LocalDateTime leftAt;

    private boolean status;  // if true user inside if false user outside
}

package uz.pdp.appjwtrealemailauditing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.pdp.appjwtrealemailauditing.entity.enums.TaskStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String title;

    @NotNull
    private String body;

    @NotNull
    private Date deadLine;

    @ManyToOne
    private User responsible;

    @CreatedBy
    private UUID createdBy;

    @CreationTimestamp
    private Timestamp createdAt;

    private TaskStatus status;

}

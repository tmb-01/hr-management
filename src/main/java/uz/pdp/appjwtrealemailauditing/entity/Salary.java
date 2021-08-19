package uz.pdp.appjwtrealemailauditing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double salaryAmount;

    @ManyToOne
    private User worker;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(nullable = false)
    private Date workStartDate;

    @Column(nullable = false)
    private Date workEndDate;

}

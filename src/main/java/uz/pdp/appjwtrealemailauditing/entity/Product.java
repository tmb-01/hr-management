package uz.pdp.appjwtrealemailauditing.entity;

import com.sun.tools.corba.se.idl.constExpr.Times;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;

    @CreatedBy
    private UUID createdBy;

    @LastModifiedBy
    private UUID updatedBy;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}

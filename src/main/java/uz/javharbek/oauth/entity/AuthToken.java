package uz.javharbek.oauth.entity;

import uz.javharbek.oauth.enums.AuthTokenStatusEnum;
import uz.javharbek.oauth.enums.AuthTokenTypeEnum;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "auth_token")
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "secret")
    private String secret;

    @Column(name = "data")
    private String data;

    @Column(name = "data_type")
    private String data_type;

    @Column(name = "type")
    private AuthTokenTypeEnum type;

    @Column(name = "status")
    private AuthTokenStatusEnum status;

    @CreationTimestamp
    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @Column(name = "expired_duration")
    private Integer expiredDuration;


    @Column(name = "identity_id")
    private String identityId;



}

package uz.javharbek.oauth.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_permission")
public class UserPermission{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "permission_id")
    private Long permissionId;
}

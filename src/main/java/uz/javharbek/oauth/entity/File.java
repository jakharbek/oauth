package uz.javharbek.oauth.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "size")
    Long size;

    @Column(name = "file")
    String file;

    @Column(name = "extension")
    String extension;

    @Column(name = "status")
    int status;

    @CreationTimestamp
    @Column(name = "created_datetime")
    LocalDateTime createdDatetime;

    @UpdateTimestamp
    @Column(name = "updated_datetime")
    LocalDateTime updatedDatetime;

    @UpdateTimestamp
    @Column(name = "deleted_datetime")
    LocalDateTime deletedDatetime;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;

    @Column(name = "host")
    String host;

}

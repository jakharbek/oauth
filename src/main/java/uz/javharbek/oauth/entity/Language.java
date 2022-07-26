package uz.javharbek.oauth.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "languages")
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "locale")
    String locale;
    @Column(name = "messagekey")
    String key;
    @Column(name = "messagecontent")
    String content;
}
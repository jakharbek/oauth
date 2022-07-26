package uz.javharbek.oauth.repository;

import uz.javharbek.oauth.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long>, CrudRepository<Language, Long> {

    Language findByKeyAndLocale(String key, String locale);

    List<Language> findLanguageByLocale(String locale);

    List<Language> findLanguageByKeyAndLocaleNot(String key,String locale);
}
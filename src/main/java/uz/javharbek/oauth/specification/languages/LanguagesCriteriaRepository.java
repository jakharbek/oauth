package uz.javharbek.oauth.specification.languages;

import uz.javharbek.oauth.dto.LanguageDTO;
import uz.javharbek.oauth.entity.Language;
import uz.javharbek.oauth.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class LanguagesCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    @Autowired
    LanguageService languageService;

    public LanguagesCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }


    public Page<LanguageDTO> findAllWithFilters(LanguagesPage LanguagesPage,
                                                LanguagesSearchCriteria LanguagesSearchCriteria) {
        CriteriaQuery<Language> criteriaQuery = criteriaBuilder.createQuery(Language.class);
        Root<Language> LanguagesRoot = criteriaQuery.from(Language.class);
        Predicate predicate = getPredicate(LanguagesSearchCriteria, LanguagesRoot);
        criteriaQuery.where(predicate);
        setOrder(LanguagesPage, criteriaQuery, LanguagesRoot);

        TypedQuery<Language> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(LanguagesPage.getPageNumber() * LanguagesPage.getPageSize());
        typedQuery.setMaxResults(LanguagesPage.getPageSize());

        Pageable pageable = getPageable(LanguagesPage);

        long LanguagesCount = getLanguagesCount(predicate);

        List<Language> languageList = typedQuery.getResultList();

        List<LanguageDTO> newResult = new ArrayList<>();

        if (!languageList.isEmpty()) {
            for (Language language : languageList) {
                LanguageDTO l = new LanguageDTO();
                l.setId(language.getId());
                l.setLocale(language.getLocale());
                l.setContent(language.getContent());
                l.setKey(language.getKey());
                l.setTranslations(languageService.getTranslations(language));
                newResult.add(l);
            }
        }
        return new PageImpl<>(newResult, pageable, LanguagesCount);
    }

    private Predicate getPredicate(LanguagesSearchCriteria LanguagesSearchCriteria,
                                   Root<Language> LanguagesRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(LanguagesSearchCriteria.getContent())) {
            predicates.add(
                    criteriaBuilder.like(LanguagesRoot.get("content"),
                            "%" + LanguagesSearchCriteria.getContent() + "%")
            );
        }
        if (Objects.nonNull(LanguagesSearchCriteria.getKey())) {
            predicates.add(
                    criteriaBuilder.like(LanguagesRoot.get("key"),
                            "%" + LanguagesSearchCriteria.getKey() + "%")
            );
        }
        if (Objects.nonNull(LanguagesSearchCriteria.getLocale())) {
            predicates.add(
                    criteriaBuilder.like(LanguagesRoot.get("locale"),
                            "%" + LanguagesSearchCriteria.getLocale() + "%")
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(LanguagesPage LanguagesPage,
                          CriteriaQuery<Language> criteriaQuery,
                          Root<Language> LanguagesRoot) {
        if (LanguagesPage.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(LanguagesRoot.get(LanguagesPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(LanguagesRoot.get(LanguagesPage.getSortBy())));
        }
    }

    private Pageable getPageable(LanguagesPage LanguagesPage) {
        Sort sort = Sort.by(LanguagesPage.getSortDirection(), LanguagesPage.getSortBy());
        return PageRequest.of(LanguagesPage.getPageNumber(), LanguagesPage.getPageSize(), sort);
    }

    private long getLanguagesCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Language> countRoot = countQuery.from(Language.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

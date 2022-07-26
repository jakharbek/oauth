package uz.javharbek.oauth.specification.authPermission;

import uz.javharbek.oauth.entity.AuthPermission;
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
public class AuthPermissionCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public AuthPermissionCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<AuthPermission> findAllWithFilters(AuthPermissionPage AuthPermissionPage,
                                                   AuthPermissionSearchCriteria AuthPermissionSearchCriteria){
        CriteriaQuery<AuthPermission> criteriaQuery = criteriaBuilder.createQuery(AuthPermission.class);
        Root<AuthPermission> AuthPermissionRoot = criteriaQuery.from(AuthPermission.class);
        Predicate predicate = getPredicate(AuthPermissionSearchCriteria, AuthPermissionRoot);
        criteriaQuery.where(predicate);
        setOrder(AuthPermissionPage, criteriaQuery, AuthPermissionRoot);

        TypedQuery<AuthPermission> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(AuthPermissionPage.getPageNumber() * AuthPermissionPage.getPageSize());
        typedQuery.setMaxResults(AuthPermissionPage.getPageSize());

        Pageable pageable = getPageable(AuthPermissionPage);

        long AuthPermissionCount = getAuthPermissionCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, AuthPermissionCount);
    }

    private Predicate getPredicate(AuthPermissionSearchCriteria AuthPermissionSearchCriteria,
                                   Root<AuthPermission> AuthPermissionRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(AuthPermissionSearchCriteria.getPermissionName())){
            predicates.add(
                    criteriaBuilder.like(AuthPermissionRoot.get("permissionName"),
                            "%" + AuthPermissionSearchCriteria.getPermissionName() + "%")
            );
        }
        if(Objects.nonNull(AuthPermissionSearchCriteria.getDescription())){
            predicates.add(
                    criteriaBuilder.like(AuthPermissionRoot.get("description"),
                            "%" + AuthPermissionSearchCriteria.getDescription() + "%")
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(AuthPermissionPage AuthPermissionPage,
                          CriteriaQuery<AuthPermission> criteriaQuery,
                          Root<AuthPermission> AuthPermissionRoot) {
        if(AuthPermissionPage.getSortDirection().equals(Sort.Direction.ASC)){
            criteriaQuery.orderBy(criteriaBuilder.asc(AuthPermissionRoot.get(AuthPermissionPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(AuthPermissionRoot.get(AuthPermissionPage.getSortBy())));
        }
    }

    private Pageable getPageable(AuthPermissionPage AuthPermissionPage) {
        Sort sort = Sort.by(AuthPermissionPage.getSortDirection(), AuthPermissionPage.getSortBy());
        return PageRequest.of(AuthPermissionPage.getPageNumber(),AuthPermissionPage.getPageSize(), sort);
    }

    private long getAuthPermissionCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<AuthPermission> countRoot = countQuery.from(AuthPermission.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

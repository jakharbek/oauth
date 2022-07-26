package uz.javharbek.oauth.specification.authRole;

import uz.javharbek.oauth.entity.AuthRole;
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
public class AuthRoleCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public AuthRoleCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<AuthRole> findAllWithFilters(AuthRolePage authRolePage,
                                             AuthRoleSearchCriteria authRoleSearchCriteria){
        CriteriaQuery<AuthRole> criteriaQuery = criteriaBuilder.createQuery(AuthRole.class);
        Root<AuthRole> authRoleRoot = criteriaQuery.from(AuthRole.class);
        Predicate predicate = getPredicate(authRoleSearchCriteria, authRoleRoot);
        criteriaQuery.where(predicate);
        setOrder(authRolePage, criteriaQuery, authRoleRoot);

        TypedQuery<AuthRole> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(authRolePage.getPageNumber() * authRolePage.getPageSize());
        typedQuery.setMaxResults(authRolePage.getPageSize());

        Pageable pageable = getPageable(authRolePage);

        long authRoleCount = getAuthRoleCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, authRoleCount);
    }

    private Predicate getPredicate(AuthRoleSearchCriteria authRoleSearchCriteria,
                                   Root<AuthRole> authRoleRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(authRoleSearchCriteria.getRoleName())){
            predicates.add(
                    criteriaBuilder.like(authRoleRoot.get("roleName"),
                            "%" + authRoleSearchCriteria.getRoleName() + "%")
            );
        }
        if(Objects.nonNull(authRoleSearchCriteria.getDescription())){
            predicates.add(
                    criteriaBuilder.like(authRoleRoot.get("description"),
                            "%" + authRoleSearchCriteria.getDescription() + "%")
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(AuthRolePage authRolePage,
                          CriteriaQuery<AuthRole> criteriaQuery,
                          Root<AuthRole> authRoleRoot) {
        if(authRolePage.getSortDirection().equals(Sort.Direction.ASC)){
            criteriaQuery.orderBy(criteriaBuilder.asc(authRoleRoot.get(authRolePage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(authRoleRoot.get(authRolePage.getSortBy())));
        }
    }

    private Pageable getPageable(AuthRolePage authRolePage) {
        Sort sort = Sort.by(authRolePage.getSortDirection(), authRolePage.getSortBy());
        return PageRequest.of(authRolePage.getPageNumber(),authRolePage.getPageSize(), sort);
    }

    private long getAuthRoleCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<AuthRole> countRoot = countQuery.from(AuthRole.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

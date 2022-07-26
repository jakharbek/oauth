package uz.javharbek.oauth.specification.authUser;

import uz.javharbek.oauth.dto.UserItemDTO;
import uz.javharbek.oauth.entity.AuthUser;
import uz.javharbek.oauth.exceptions.CdnServerNotFoundException;
import uz.javharbek.oauth.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AuthUserCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    FileService fileService;

    public AuthUserCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<UserItemDTO> findAllWithFilters(AuthUserPage AuthUserPage,
                                                AuthUserSearchCriteria AuthUserSearchCriteria) throws CdnServerNotFoundException {
        CriteriaQuery<AuthUser> criteriaQuery = criteriaBuilder.createQuery(AuthUser.class);
        Root<AuthUser> AuthUserRoot = criteriaQuery.from(AuthUser.class);
        Predicate predicate = getPredicate(AuthUserSearchCriteria, AuthUserRoot);
        criteriaQuery.where(predicate);
        setOrder(AuthUserPage, criteriaQuery, AuthUserRoot);

        TypedQuery<AuthUser> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(AuthUserPage.getPageNumber() * AuthUserPage.getPageSize());
        typedQuery.setMaxResults(AuthUserPage.getPageSize());

        Pageable pageable = getPageable(AuthUserPage);

        long AuthUserCount = getAuthUserCount(predicate);

        List<AuthUser> authUserList = typedQuery.getResultList();

        List<UserItemDTO> userItemDTOS = new ArrayList<>();

        if (!authUserList.isEmpty()) {
            for (AuthUser authUser : authUserList) {
                UserItemDTO userItemDTO = objectMapper.convertValue(authUser, UserItemDTO.class);
                if (userItemDTO.getAvatar() != null) {
                    userItemDTO.setAvatarUrl(fileService.getAbsoluteUrl(userItemDTO.getAvatar()));
                }
                userItemDTOS.add(userItemDTO);
            }
        }

        return new PageImpl<>(userItemDTOS, pageable, AuthUserCount);
    }

    private Predicate getPredicate(AuthUserSearchCriteria AuthUserSearchCriteria,
                                   Root<AuthUser> AuthUserRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(AuthUserSearchCriteria.getUserName())) {
            predicates.add(
                    criteriaBuilder.like(AuthUserRoot.get("userName"),
                            "%" + AuthUserSearchCriteria.getUserName() + "%")
            );
        }
        if (Objects.nonNull(AuthUserSearchCriteria.getPhone())) {
            predicates.add(
                    criteriaBuilder.like(AuthUserRoot.get("phone"),
                            "%" + AuthUserSearchCriteria.getPhone() + "%")
            );
        }
        if (Objects.nonNull(AuthUserSearchCriteria.getEmail())) {
            predicates.add(
                    criteriaBuilder.like(AuthUserRoot.get("email"),
                            "%" + AuthUserSearchCriteria.getEmail() + "%")
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(AuthUserPage AuthUserPage,
                          CriteriaQuery<AuthUser> criteriaQuery,
                          Root<AuthUser> AuthUserRoot) {
        if (AuthUserPage.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(AuthUserRoot.get(AuthUserPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(AuthUserRoot.get(AuthUserPage.getSortBy())));
        }
    }

    private Pageable getPageable(AuthUserPage AuthUserPage) {
        Sort sort = Sort.by(AuthUserPage.getSortDirection(), AuthUserPage.getSortBy());
        return PageRequest.of(AuthUserPage.getPageNumber(), AuthUserPage.getPageSize(), sort);
    }

    private long getAuthUserCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<AuthUser> countRoot = countQuery.from(AuthUser.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

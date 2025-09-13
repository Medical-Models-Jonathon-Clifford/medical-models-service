package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.List;
import org.jono.medicalmodelsservice.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserCrudRepository extends CrudRepository<User, String> {
    @Override
    Iterable<User> findAllById(Iterable<String> ids);

    @Query("SELECT u.* FROM user u "
            + "WHERE UPPER(CONCAT(u.honorific, ' ', u.given_name, ' ', u.family_name)) "
            + "LIKE CONCAT('%', UPPER(:name), '%')")
    List<User> findByNameIsLikeIgnoreCase(String name);

    @Query("SELECT u.* FROM user u "
            + "JOIN user_company_relationship ucr ON u.id = ucr.user_id "
            + "WHERE ucr.company_id = :companyId")
    List<User> findByCompanyId(String companyId);

    @Query("SELECT u.* FROM user u "
            + "JOIN user_company_relationship ucr ON u.id = ucr.user_id "
            + "WHERE ucr.company_id = :companyId "
            + "AND UPPER(CONCAT(u.honorific, ' ', u.given_name, ' ', u.family_name)) "
            + "LIKE CONCAT('%', UPPER(:name), '%')")
    List<User> findByCompanyAndNameIsLikeIgnoreCase(String companyId, String name);
}

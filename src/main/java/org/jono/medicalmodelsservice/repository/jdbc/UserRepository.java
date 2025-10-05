package org.jono.medicalmodelsservice.repository.jdbc;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.DailyResourceCount;
import org.jono.medicalmodelsservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserRepository {

    private final UserCrudRepository userCrudRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(final UserCrudRepository userCrudRepository, final JdbcTemplate jdbcTemplate) {
        this.userCrudRepository = userCrudRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        return Lists.newArrayList(this.userCrudRepository.findAll());
    }

    public Optional<User> findById(final String id) {
        return this.userCrudRepository.findById(id);
    }

    public List<User> findByName(final String name) {
        return this.userCrudRepository.findByNameIsLikeIgnoreCase('%' + name + '%');
    }

    public List<User> findByCompanyId(final String companyId) {
        return this.userCrudRepository.findByCompanyId(companyId);
    }

    public List<User> findByCompanyAndName(final String companyId, final String name) {
        return this.userCrudRepository.findByCompanyAndNameIsLikeIgnoreCase(companyId, name);
    }

    public List<User> findUsersForIds(final List<String> ids) {
        final Iterable<User> userIterable = this.userCrudRepository.findAllById(ids);
        return Lists.newArrayList(userIterable);
    }

    public long count() {
        return this.userCrudRepository.count();
    }

    public List<DailyResourceCount> getUserGrowthData() {
        final String sql = """
                           WITH RECURSIVE dates AS (
                               SELECT CAST(MIN(created_date) AS DATE) as date
                               FROM user
                           
                               UNION ALL
                           
                               SELECT DATE_ADD(date, INTERVAL 1 DAY)
                               FROM dates
                               WHERE date < CURRENT_DATE()
                           ),
                           daily_counts AS (
                               SELECT 
                                   CAST(created_date AS DATE) as date,
                                   COUNT(*) as daily_count
                               FROM user
                               GROUP BY CAST(created_date AS DATE)
                           )
                           SELECT 
                               d.date,
                               COALESCE(dc.daily_count, 0) as new_users,
                               SUM(COALESCE(dc.daily_count, 0)) OVER (ORDER BY d.date) as total_users
                           FROM dates d
                           LEFT JOIN daily_counts dc ON d.date = dc.date
                           ORDER BY d.date
                           """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            log.debug("Mapping row: {} {} {}",
                      rs.getDate("date"),
                      rs.getLong("new_users"),
                      rs.getLong("total_users"));

            return new DailyResourceCount(
                    rs.getDate("date").toLocalDate(),
                    rs.getLong("new_users"),
                    rs.getLong("total_users")
            );
        });
    }
}

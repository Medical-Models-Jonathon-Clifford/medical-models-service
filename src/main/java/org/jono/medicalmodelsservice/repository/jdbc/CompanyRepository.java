package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.DailyResourceCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompanyRepository {

    private final CompanyCrudRepository companyCrudRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CompanyRepository(final CompanyCrudRepository companyCrudRepository, final JdbcTemplate jdbcTemplate) {
        this.companyCrudRepository = companyCrudRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count() {
        return companyCrudRepository.count();
    }

    public List<DailyResourceCount> getCompanyGrowthData() {
        final String sql = """
                           WITH RECURSIVE dates AS (
                               SELECT CAST(MIN(created_date) AS DATE) as date
                               FROM company
                           
                               UNION ALL
                           
                               SELECT DATE_ADD(date, INTERVAL 1 DAY)
                               FROM dates
                               WHERE date < CURRENT_DATE()
                           ),
                           daily_counts AS (
                               SELECT 
                                   CAST(created_date AS DATE) as date,
                                   COUNT(*) as daily_count
                               FROM company
                               GROUP BY CAST(created_date AS DATE)
                           )
                           SELECT 
                               d.date,
                               COALESCE(dc.daily_count, 0) as new_companies,
                               SUM(COALESCE(dc.daily_count, 0)) OVER (ORDER BY d.date) as total_companies
                           FROM dates d
                           LEFT JOIN daily_counts dc ON d.date = dc.date
                           ORDER BY d.date
                           """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            log.debug("Mapping row: {} {} {}",
                      rs.getDate("date"),
                      rs.getLong("new_companies"),
                      rs.getLong("total_companies"));

            return new DailyResourceCount(
                    rs.getDate("date").toLocalDate(),
                    rs.getLong("new_companies"),
                    rs.getLong("total_companies")
            );
        });
    }
}

package org.jono.medicalmodelsservice.repository.jdbc;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Company;
import org.jono.medicalmodelsservice.model.DailyResourceCount;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyRepository {

    private final CompanyCrudRepository companyCrudRepository;
    private final JdbcTemplate jdbcTemplate;

    public long count() {
        return companyCrudRepository.count();
    }

    public Company findById(final String companyId) {
        final Optional<Company> optionalCompany = companyCrudRepository.findById(companyId);
        if (optionalCompany.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found with id: " + companyId);
        }
        return optionalCompany.get();
    }

    public List<Company> findAll() {
        return Lists.newArrayList(companyCrudRepository.findAll());
    }

    public List<Company> findByNameAndState(final String nameSearchTerm, final String stateFilter) {
        return companyCrudRepository.findByNameIsLikeIgnoreCaseAndLocationStateEqualsIgnoreCase(
                '%' + nameSearchTerm + '%', stateFilter);
    }

    public List<Company> findByName(final String nameSearchTerm) {
        return companyCrudRepository.findByNameIsLikeIgnoreCase('%' + nameSearchTerm + '%');
    }

    public List<Company> findByState(final String stateFilter) {
        return companyCrudRepository.findByLocationStateEqualsIgnoreCase(stateFilter);
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

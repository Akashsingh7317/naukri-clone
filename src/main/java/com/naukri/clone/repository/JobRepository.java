package com.naukri.clone.repository;

import com.naukri.clone.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByActiveTrue();
    List<Job> findByEmployerId(Long employerId);
    List<Job> findByActiveTrueOrderByPostedAtDesc();

    @Query(value = """
        SELECT * FROM jobs WHERE active = true
        AND (
            CAST(:keyword AS TEXT) IS NULL
            OR LOWER(CAST(title AS TEXT)) LIKE LOWER(CONCAT('%', CAST(:keyword AS TEXT), '%'))
            OR LOWER(CAST(company AS TEXT)) LIKE LOWER(CONCAT('%', CAST(:keyword AS TEXT), '%'))
            OR LOWER(CAST(skills AS TEXT)) LIKE LOWER(CONCAT('%', CAST(:keyword AS TEXT), '%'))
        )
        AND (
            CAST(:location AS TEXT) IS NULL
            OR LOWER(CAST(location AS TEXT)) LIKE LOWER(CONCAT('%', CAST(:location AS TEXT), '%'))
        )
        AND (
            CAST(:category AS TEXT) IS NULL
            OR CAST(category AS TEXT) = CAST(:category AS TEXT)
        )
        """, nativeQuery = true)
    List<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("category") String category);
}

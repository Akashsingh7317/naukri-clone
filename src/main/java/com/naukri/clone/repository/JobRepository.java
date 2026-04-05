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

    @Query("SELECT j FROM Job j WHERE j.active = true AND " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.skills) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:category IS NULL OR j.category = :category)")
    List<Job> searchJobs(@Param("keyword") String keyword,
                          @Param("location") String location,
                          @Param("category") String category);

    List<Job> findByActiveTrueOrderByPostedAtDesc();
}

package com.naukri.clone.repository;
import com.naukri.clone.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUserIdOrderByEndYearDesc(Long userId);
}

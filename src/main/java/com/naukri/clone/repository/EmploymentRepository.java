package com.naukri.clone.repository;
import com.naukri.clone.model.Employment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    List<Employment> findByUserIdOrderByStartDateDesc(Long userId);
}

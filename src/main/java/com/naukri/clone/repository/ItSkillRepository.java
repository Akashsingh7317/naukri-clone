package com.naukri.clone.repository;
import com.naukri.clone.model.ItSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ItSkillRepository extends JpaRepository<ItSkill, Long> {
    List<ItSkill> findByUserId(Long userId);
}

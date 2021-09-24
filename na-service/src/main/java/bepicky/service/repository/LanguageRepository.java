package bepicky.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.LanguageEntity;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageEntity, String>, JpaSpecificationExecutor<LanguageEntity> {
}

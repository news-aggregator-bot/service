package bepicky.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.ContentBlockEntity;
import bepicky.service.entity.SourcePageEntity;

import java.util.List;

@Repository
public interface ContentBlockRepository extends JpaRepository<ContentBlockEntity, Long> {

    List<ContentBlockEntity> findBySourcePage(SourcePageEntity page);
}

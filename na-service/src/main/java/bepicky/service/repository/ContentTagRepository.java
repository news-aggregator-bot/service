package bepicky.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.ContentTagEntity;

import java.util.List;

@Repository
public interface ContentTagRepository extends JpaRepository<ContentTagEntity, Long> {

    List<ContentTagEntity> findByValue(String value);
}

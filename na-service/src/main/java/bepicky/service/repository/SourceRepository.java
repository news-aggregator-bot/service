package bepicky.service.repository;

import bepicky.service.entity.SourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<SourceEntity, Long> {

    Optional<SourceEntity> findByName(String name);

    List<SourceEntity> findByStatusNotAndFetchPeriod(SourceEntity.Status status, SourceEntity.FetchPeriod fetchPeriod);

    Page<SourceEntity> findByStatusOrderByNameAsc(SourceEntity.Status status, Pageable pageable);
}

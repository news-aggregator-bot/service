package bepicky.service.repository;

import bepicky.service.entity.Source;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {

    Optional<Source> findByName(String name);

    List<Source> findByStatusNotAndFetchPeriod(Source.Status status, Source.FetchPeriod fetchPeriod);

    Page<Source> findByStatusNotOrderByNameAsc(Source.Status status, Pageable pageable);
}

package vlad110kg.news.aggregator.repository;

import java.util.Optional;

import vlad110kg.news.aggregator.entity.Source;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {

    Optional<Source> findByName(String name);
}

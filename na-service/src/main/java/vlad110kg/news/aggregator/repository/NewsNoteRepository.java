package vlad110kg.news.aggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vlad110kg.news.aggregator.entity.NewsNote;

import java.util.Optional;

@Repository
public interface NewsNoteRepository extends JpaRepository<NewsNote, Long> {

    Optional<NewsNote> findByUrl(String url);

    boolean existsByUrl(String url);
}

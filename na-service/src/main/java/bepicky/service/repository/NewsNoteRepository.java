package bepicky.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.NewsNote;

import java.util.Optional;

@Repository
public interface NewsNoteRepository extends JpaRepository<NewsNote, Long> {

    Optional<NewsNote> findByUrl(String url);

    boolean existsByUrl(String url);
}

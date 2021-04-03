package bepicky.service.repository;

import bepicky.service.entity.Tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNormalisedValue(String value);

    List<Tag> findByNormalisedValueContains(String value);
}

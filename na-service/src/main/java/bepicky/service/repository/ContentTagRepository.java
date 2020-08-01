package bepicky.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.ContentTag;

import java.util.List;

@Repository
public interface ContentTagRepository extends JpaRepository<ContentTag, Long> {

    List<ContentTag> findByValue(String value);
}

package bepicky.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.SourcePage;

import java.util.List;

@Repository
public interface ContentBlockRepository extends JpaRepository<ContentBlock, Long> {

    List<ContentBlock> findBySourcePage(SourcePage page);
}

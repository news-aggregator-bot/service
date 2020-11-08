package bepicky.service.repository;

import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByName(String name);

    Page<Category> findAllByIdIn(Iterable<Long> id, Pageable pageable);

    Page<Category> findAllByIdInAndIdNotIn(Iterable<Long> id, Iterable<Long> notInId, Pageable pageable);

    Page<Category> findAllByReaders_IdAndIdIn(long id, Iterable<Long> ids, Pageable pageable);

    Page<Category> findAllByParent(Category parent, Pageable pageable);

    List<Category> findAllByParent(Category parent);

    Page<Category> findAllByTypeAndParentIsNull(CategoryType type, Pageable pageable);

    List<Category> findAllByTypeAndParentIsNull(CategoryType type);

    List<Category> findAllByReaders_IdAndType(long id, CategoryType type);

    List<Category> findAllByReaders_IdAndParent(long id, Category parent);

    Page<Category> findAllByReaders_IdAndParent(long id, Category parent, Pageable pageable);

}

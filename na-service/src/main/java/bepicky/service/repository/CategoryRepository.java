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

    List<Category> findAllByType(CategoryType type);

    Page<Category> findAllByIdInAndIdNotIn(Iterable<Long> id, Iterable<Long> notInId, Pageable pageable);

    Page<Category> findAllByParentOrderByNameAsc(Category parent, Pageable pageable);

    List<Category> findAllByParent(Category parent);

    Page<Category> findAllByTypeAndParentIsNullOrderByNameAsc(CategoryType type, Pageable pageable);

    List<Category> findAllByReaders_IdAndParent(long id, Category parent);

    Page<Category> findAllByReaders_IdAndParent(long id, Category parent, Pageable pageable);

}

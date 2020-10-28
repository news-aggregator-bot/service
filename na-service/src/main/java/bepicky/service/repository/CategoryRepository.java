package bepicky.service.repository;

import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByName(String name);

    Page<Category> findAllByParent(Category parent, Pageable pageable);

    Page<Category> findAllByTypeAndParentIsNull(CategoryType type, Pageable pageable);

    @Query("select c from Category c join c.readers as r with r.id = ?1 where c.parent is NULL and c.type = ?2")
    Page<Category> findPickedTopCategories(long readerId, CategoryType type, Pageable pageable);

    @Query("select c from Category c join c.readers as r with r.id = ?1 where c.parent = ?2")
    Page<Category> findPickedCategoriesByParent(long readerId, Category parent, Pageable pageable);

    @Query("select c from Category c join c.readers as r with r.id != ?1 where c.parent is NULL and c.type = ?2")
    Page<Category> findNotPickedTopCategories(long readerId, CategoryType type, Pageable pageable);

    @Query("select c from Category c join c.readers as r with r.id != ?1 where c.parent = ?2")
    Page<Category> findNotPickedCategoriesByParent(long readerId, Category parent, Pageable pageable);

}

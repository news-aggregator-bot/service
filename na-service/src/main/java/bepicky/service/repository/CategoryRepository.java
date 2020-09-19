package bepicky.service.repository;

import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByName(String name);

    Page<Category> findAllByParent(Category parent, Pageable pageable);

    Page<Category> findAllByTypeAndParentIsNull(CategoryType type, Pageable pageable);

}

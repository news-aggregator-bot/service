package bepicky.service.repository;

import bepicky.service.entity.Category;
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

    Page<Category> findAllByParentIsNull(Pageable pageable);

    long countByParentIsNull();

    long countByParent(Category parent);
}

package bepicky.service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByName(String name);

    List<Category> findAllByParent(Category parent, Pageable pageable);

    List<Category> findAllByParentIsNull(Pageable pageable);

    long countByParentIsNull();

    long countByParent(Category parent);
}

package bepicky.service.repository;

import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, JpaSpecificationExecutor<CategoryEntity> {

    Optional<CategoryEntity> findByName(String name);

    List<CategoryEntity> findAllByType(CategoryType type);

    Page<CategoryEntity> findAllByIdInAndIdNotIn(Iterable<Long> id, Iterable<Long> notInId, Pageable pageable);

    Page<CategoryEntity> findAllByParentOrderByNameAsc(CategoryEntity parent, Pageable pageable);

    List<CategoryEntity> findAllByParent(CategoryEntity parent);

    Page<CategoryEntity> findAllByTypeAndParentIsNullOrderByNameAsc(CategoryType type, Pageable pageable);

    List<CategoryEntity> findAllByReaders_IdAndParent(long id, CategoryEntity parent);

    Page<CategoryEntity> findAllByReaders_IdAndParent(long id, CategoryEntity parent, Pageable pageable);

}

package bepicky.service.service;

import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ICategoryService {

    CategoryEntity save(CategoryEntity category);

    List<CategoryEntity> saveAll(Collection<CategoryEntity> categories);

    List<CategoryEntity> getAll();

    Set<CategoryEntity> getAllByType(CategoryType type);

    Page<CategoryEntity> findByParent(CategoryEntity parent, Pageable pageable);

    Page<CategoryEntity> findTopCategories(CategoryType type, Pageable pageable);

    Optional<CategoryEntity> find(Long id);

    Optional<CategoryEntity> findByName(String name);

    Optional<CategoryEntity> delete(long id);

    Optional<CategoryEntity> deleteByName(String name);

}

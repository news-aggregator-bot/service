package bepicky.service.service;

import bepicky.service.entity.Category;
import bepicky.service.entity.Localisation;
import bepicky.service.entity.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ICategoryService {

    Category save(Category category);

    List<Category> saveAll(Collection<Category> categories);

    List<Category> getAll();

    Set<Category> getAllByType(CategoryType type);

    Page<Category> findByParent(Category parent, Pageable pageable);

    Page<Category> findTopCategories(CategoryType type, Pageable pageable);

    Optional<Category> find(Long id);

    Optional<Category> findByName(String name);

    Optional<Category> delete(long id);

    Optional<Category> deleteByName(String name);

}

package bepicky.service.service;

import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryLocalisation;
import bepicky.service.entity.CategoryType;
import bepicky.service.entity.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    Category save(Category category);

    List<Category> saveAll(Collection<Category> categories);

    List<Category> getAll();

    Page<Category> findByParent(Category parent, Pageable pageable);

    Page<Category> findTopCategories(CategoryType type, Pageable pageable);

    Page<Category> findPickedTopCategories(Reader reader, CategoryType type, Pageable pageable);

    Page<Category> findPickedCategoriesByParent(Reader reader, Category parent, Pageable pageable);

    Page<Category> findNotPickedTopCategories(Reader reader, CategoryType type, Pageable pageable);

    Page<Category> findNotPickedCategoriesByParent(Reader reader, Category parent, Pageable pageable);

    Optional<Category> find(Long id);

    Optional<Category> findByName(String name);

    Optional<Category> delete(long id);

    Optional<Category> deleteByName(String name);

    List<CategoryLocalisation> saveAllLocalisations(Collection<CategoryLocalisation> categories);

    List<CategoryLocalisation> findLocalisationByValue(String value);
}

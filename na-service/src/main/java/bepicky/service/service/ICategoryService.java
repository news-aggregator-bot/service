package bepicky.service.service;

import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryLocalisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    Category save(Category category);

    List<Category> saveAll(Collection<Category> categories);

    List<Category> findAll(Pageable pageable);

    Page<Category> findByParent(Category parent, Pageable pageable);

    long countByParent(Category parent);

    Page<Category> findTopCategories(Pageable pageable);

    long countAll();

    long countTopCategories();

    Optional<Category> find(Long id);

    Optional<Category> findByName(String name);

    void delete(long id);

    List<CategoryLocalisation> saveAllLocalisations(Collection<CategoryLocalisation> categories);

    List<CategoryLocalisation> findLocalisationByValue(String value);
}

package vlad110kg.news.aggregator.service;

import org.springframework.data.domain.Pageable;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.CategoryLocalisation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    Category save(Category category);

    List<Category> saveAll(Collection<Category> categories);

    List<Category> findAll(Pageable pageable);

    List<Category> findByParent(Category parent, Pageable pageable);

    long countByParent(Category parent);

    List<Category> findTopCategories(Pageable pageable);

    long countAll();

    long countTopCategories();

    Optional<Category> find(Long id);

    Optional<Category> findByName(String name);

    void delete(long id);

    List<CategoryLocalisation> saveAllLocalisations(Collection<CategoryLocalisation> categories);

    List<CategoryLocalisation> findLocalisationByValue(String value);
}

package vlad110kg.news.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.CategoryLocalisation;
import vlad110kg.news.aggregator.repository.CategoryLocalisationRepository;
import vlad110kg.news.aggregator.repository.CategoryRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private CategoryLocalisationRepository localisationRepository;

    @Override
    public Category save(Category category) {
        log.info("category:save:{}", category);
        return repository.save(category);
    }

    @Override
    public List<Category> saveAll(Collection<Category> categories) {
        log.info("category:save:{}", categories);
        return repository.saveAll(categories);
    }

    @Override
    public List<Category> findAll(Pageable pageable) {
        return repository.findAll(pageable).toList();
    }

    @Override
    public List<Category> findByParent(Category parent, Pageable pageable) {
        return repository.findAllByParent(parent, pageable);
    }

    @Override
    public long countByParent(Category parent) {
        return repository.countByParent(parent);
    }

    @Override
    public List<Category> findTopCategories(Pageable pageable) {
        return repository.findAllByParentIsNull(pageable);
    }

    @Override
    public long countAll() {
        return repository.count();
    }

    @Override
    public long countTopCategories() {
        return repository.countByParentIsNull();
    }

    @Override
    public Optional<Category> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public void delete(long id) {
        find(id).ifPresent(c -> repository.deleteById(id));
    }

    @Override
    public List<CategoryLocalisation> saveAllLocalisations(Collection<CategoryLocalisation> categories) {
        log.info("categorylocalisation:save:{}", categories);
        return localisationRepository.saveAll(categories);
    }

    @Override
    public List<CategoryLocalisation> findLocalisationByValue(String value) {
        return localisationRepository.findByValue(value);
    }
}

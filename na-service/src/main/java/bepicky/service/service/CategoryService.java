package bepicky.service.service;

import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryLocalisation;
import bepicky.service.entity.CategoryType;
import bepicky.service.repository.CategoryLocalisationRepository;
import bepicky.service.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
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
    public Page<Category> findByParent(Category parent, Pageable pageable) {
        return repository.findAllByParent(parent, pageable);
    }

    @Override
    public Page<Category> findTopCategories(CategoryType type, Pageable pageable) {
        return repository.findAllByTypeAndParentIsNull(type, pageable);
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

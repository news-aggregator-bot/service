package bepicky.service.service;

import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryType;
import bepicky.service.repository.LocalisationRepository;
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
    public List<Category> getAll() {
        return repository.findAll();
    }

    @Override
    public Page<Category> findByParent(Category parent, Pageable pageable) {
        return repository.findAllByParentOrderByNameAsc(parent, pageable);
    }

    @Override
    public Page<Category> findTopCategories(CategoryType type, Pageable pageable) {
        return repository.findAllByTypeAndParentIsNullOrderByNameAsc(
            type,
            pageable
        );
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
    public Optional<Category> delete(long id) {
        return find(id).map(c -> {
            repository.delete(c);
            return c;
        });
    }

    @Override
    public Optional<Category> deleteByName(String name) {
        return findByName(name).map(c -> {
            repository.delete(c);
            return c;
        });
    }

}

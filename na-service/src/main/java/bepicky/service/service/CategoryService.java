package bepicky.service.service;

import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.CategoryType;
import bepicky.service.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository repository;

    @Override
    public CategoryEntity save(CategoryEntity category) {
        log.info("category:save:{}", category);
        return repository.save(category);
    }

    @Override
    public List<CategoryEntity> saveAll(Collection<CategoryEntity> categories) {
        log.info("category:save:{}", categories);
        return repository.saveAll(categories);
    }

    @Override
    public List<CategoryEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public Set<CategoryEntity> getAllByType(CategoryType type) {
        return new HashSet<>(repository.findAllByType(type));
    }

    @Override
    public Page<CategoryEntity> findByParent(CategoryEntity parent, Pageable pageable) {
        return repository.findAllByParentOrderByNameAsc(parent, pageable);
    }

    @Override
    public Page<CategoryEntity> findTopCategories(CategoryType type, Pageable pageable) {
        return repository.findAllByTypeAndParentIsNullOrderByNameAsc(
            type,
            pageable
        );
    }

    @Override
    public Optional<CategoryEntity> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<CategoryEntity> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Optional<CategoryEntity> delete(long id) {
        return find(id).map(c -> {
            repository.delete(c);
            return c;
        });
    }

    @Override
    public Optional<CategoryEntity> deleteByName(String name) {
        return findByName(name).map(c -> {
            repository.delete(c);
            return c;
        });
    }

}

package bepicky.service.service;

import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryLocalisation;
import bepicky.service.entity.CategoryType;
import bepicky.service.entity.Reader;
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
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public List<CategoryLocalisation> saveAllLocalisations(Collection<CategoryLocalisation> categories) {
        log.info("categorylocalisation:save:{}", categories);
        return localisationRepository.saveAll(categories);
    }

    // NOT USED
    @Override
    public Page<Category> findPickedTopCategories(Reader reader, CategoryType type, Pageable pageable) {
        List<Category> picked = repository.findAllByReaders_IdAndType(reader.getId(), type);
        if (picked.isEmpty()) {
            return Page.empty(pageable);
        }

        Set<Category> pickedParents = picked.stream().map(this::takeParent).collect(Collectors.toSet());
        if (pickedParents.size() == 1) {
            return repository.findAllByReaders_IdAndIdIn(
                reader.getId(),
                collectSubcategoryIds(pickedParents),
                pageable
            );
        }
        return findByIds(pageable, pickedParents);
    }

    @Override
    public Page<Category> findPickedCategoriesByParent(
        Reader reader, Category parent, Pageable pageable
    ) {
        return repository.findAllByReaders_IdAndParent(reader.getId(), parent, pageable);
    }

    @Override
    public Page<Category> findNotPickedTopCategories(Reader reader, CategoryType type, Pageable pageable) {
        List<Category> picked = repository.findAllByReaders_IdAndType(reader.getId(), type);
        if (picked.isEmpty()) {
            return Page.empty(pageable);
        }
        Set<Long> topCategoriesIds = repository.findAllByTypeAndParentIsNull(type)
            .stream()
            .map(Category::getId)
            .collect(Collectors.toSet());
        Set<Long> pickedIds = picked.stream().map(Category::getId).collect(Collectors.toSet());
        return repository.findAllByIdInAndIdNotIn(topCategoriesIds, pickedIds, pageable);
    }

    @Override
    public Page<Category> findNotPickedCategoriesByParent(
        Reader reader, Category parent, Pageable pageable
    ) {
        List<Category> pickedByParent = repository.findAllByReaders_IdAndParent(reader.getId(), parent);
        if (pickedByParent.isEmpty()) {
            return repository.findAllByParentOrderByNameAsc(parent, pageable);
        }
        Set<Long> parentIds = repository.findAllByParent(parent).stream()
            .map(Category::getId)
            .collect(Collectors.toSet());
        Set<Long> pickedIds = pickedByParent.stream().map(Category::getId).collect(Collectors.toSet());
        return repository.findAllByIdInAndIdNotIn(parentIds, pickedIds, pageable);
    }

    @Override
    public List<CategoryLocalisation> findLocalisationByValue(String value) {
        return localisationRepository.findByValue(value);
    }

    private Set<Long> collectSubcategoryIds(Set<Category> pickedParents) {
        return pickedParents.stream()
            .flatMap(c -> c.getSubcategories().stream())
            .map(Category::getId)
            .collect(Collectors.toSet());
    }

    private Page<Category> findByIds(Pageable pageable, Set<Category> categories) {
        Set<Long> categoryIds = categories.stream()
            .map(Category::getId)
            .collect(Collectors.toSet());
        return repository.findAllByIdIn(categoryIds, pageable);
    }

    private Category takeParent(Category c) {
        return c.getParent() == null ? c : takeParent(c.getParent());
    }
}

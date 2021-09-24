package bepicky.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.entity.ContentTagEntity;
import bepicky.service.repository.ContentTagRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class ContentTagService implements IContentTagService {

    @Autowired
    private ContentTagRepository repository;

    @Override
    public List<ContentTagEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ContentTagEntity> findByIds(Collection<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<ContentTagEntity> findByValue(String value) {
        return repository.findByValue(value);
    }

    @Override
    public Optional<ContentTagEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public ContentTagEntity save(ContentTagEntity tag) {
        return repository.save(tag);
    }

    @Override
    @Transactional
    public List<ContentTagEntity> saveAll(Collection<ContentTagEntity> tags) {
        return repository.saveAll(tags);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(p -> repository.deleteById(id));
    }
}

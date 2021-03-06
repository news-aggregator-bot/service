package bepicky.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.entity.ContentTag;
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
    public List<ContentTag> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ContentTag> findByIds(Collection<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<ContentTag> findByValue(String value) {
        return repository.findByValue(value);
    }

    @Override
    public Optional<ContentTag> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public ContentTag save(ContentTag tag) {
        return repository.save(tag);
    }

    @Override
    @Transactional
    public List<ContentTag> saveAll(Collection<ContentTag> tags) {
        return repository.saveAll(tags);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(p -> repository.deleteById(id));
    }
}

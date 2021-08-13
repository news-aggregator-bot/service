package bepicky.service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class NewsNoteNativeRepository {

    @Autowired
    private EntityManager entityManager;

    public List<Long> find(Set<String> keys) {
        StringBuilder b = new StringBuilder("select id from news_note where ");
        keys.forEach(k -> b.append("normalised_title like '%").append(k).append("%' and "));
        b.delete(b.length() -4, b.length());
        Query q = entityManager.createNativeQuery(b.toString());
        return q.getResultList()
            .stream()
            .mapToLong(id ->  Long.valueOf(id.toString()))
            .boxed()
            .collect(Collectors.toList());
    }
}

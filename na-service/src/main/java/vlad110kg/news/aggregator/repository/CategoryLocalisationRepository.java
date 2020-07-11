package vlad110kg.news.aggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vlad110kg.news.aggregator.entity.CategoryLocalisation;

import java.util.List;

@Repository
public interface CategoryLocalisationRepository extends JpaRepository<CategoryLocalisation, Long> {

    List<CategoryLocalisation> findByValue(String value);
}

package vlad110kg.news.aggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vlad110kg.news.aggregator.entity.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, String>, JpaSpecificationExecutor<Language> {
}

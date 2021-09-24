package bepicky.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.LocalisationEntity;

import java.util.List;

@Repository
public interface LocalisationRepository extends JpaRepository<LocalisationEntity, Long> {

    List<LocalisationEntity> findByValue(String value);

}

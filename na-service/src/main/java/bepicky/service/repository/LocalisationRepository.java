package bepicky.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.Localisation;

import java.util.List;

@Repository
public interface LocalisationRepository extends JpaRepository<Localisation, Long> {

    List<Localisation> findByValue(String value);

}

package take_home_challenge.asin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import take_home_challenge.asin.entities.Person;

import java.util.Date;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByNomContainingIgnoreCase(String nom);

    List<Person> findByStatus(String status);

    List<Person> findByMatricule(String matricule);

    List<Person> findByDateNaissance(Date dateNaissance);

    List<Person> findByDateNaissanceBefore(Date date);

    List<Person> findByDateNaissanceAfter(Date date);

    List<Person> findByDateNaissanceBetween(Date startDate, Date endDate);
}

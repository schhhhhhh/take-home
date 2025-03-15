package take_home_challenge.asin.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import take_home_challenge.asin.entities.Person;
import take_home_challenge.asin.repositories.PersonRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // @Transactional
    public void importData(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Le fichier spécifié n'existe pas.");
            return;
        }

        try (
                //InputStream inputStream = System.in;
                InputStream inputStream = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            List<Person> personList = new ArrayList<>();  // Liste pour stocker les personnes avant insertion par lots

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                /*String matricule = row.getCell(0).getStringCellValue();
                String nom = row.getCell(1).getStringCellValue();
                String prenom = row.getCell(2).getStringCellValue();
                String dateNaissanceStr = row.getCell(3).getStringCellValue();
                String status = row.getCell(4).getStringCellValue();*/

                // Utilisation de la méthode getCellValue pour récupérer les valeurs des cellules
                String matricule = getCellValue(row.getCell(0));  // Matricule
                String nom = getCellValue(row.getCell(1));        // Nom
                String prenom = getCellValue(row.getCell(2));     // Prénom
                String dateNaissanceStr = getCellValue(row.getCell(3));  // Date de naissance
                String status = getCellValue(row.getCell(4));     // Statut

                Date dateNaissance = parseDate(dateNaissanceStr);
                Person person = new Person();
                person.setMatricule(matricule);
                person.setNom(nom);
                person.setPrenom(prenom);
                person.setDateNaissance(dateNaissance);
                person.setStatus(status);

                // personRepository.save(person);

                personList.add(person);

                System.out.printf("Importé: %s, %s %s, %s, %s\n", matricule, nom, prenom, dateNaissanceStr, status);

                // Insère par lots tous les 1000 enregistrements
                if (personList.size() >= 1000) {
                    personRepository.saveAll(personList);  // Insertion par lots
                    // entityManager.flush();
                    // entityManager.clear();
                    personList.clear();  // Vide la liste pour le prochain lot
                }
                // System.out.printf("Importé: %s, %s %s, %s, %s\n", matricule, nom, prenom, dateNaissanceStr, status);
            }

            // Enregistre les restes s'il y en a après la boucle
            if (!personList.isEmpty()) {
                personRepository.saveAll(personList);
                // entityManager.flush();
                // entityManager.clear();
            }

            System.out.println("Importation terminée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void importData2(String filePath) {
        List<Person> personList = new ArrayList<>();
        int totalCount = 0;
        int errorCount = 0;

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Le fichier spécifié n'existe pas.");
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int batchSize = 1000; // Réduit la taille des lots pour optimiser la mémoire

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorer l'entête

                try {
                    String matricule = getCellValue(row.getCell(0));
                    String nom = getCellValue(row.getCell(1));
                    String prenoms = getCellValue(row.getCell(2));
                    String dateNaissanceStr = getCellValue(row.getCell(3));
                    Date dateNaissance = parseDate(dateNaissanceStr);

                    if (matricule == null || matricule.isEmpty() || nom == null || nom.isEmpty()) {
                        System.err.println("Ligne ignorée (valeurs manquantes) : " + row.getRowNum());
                        errorCount++;
                        continue;
                    }

                    Person person = new Person();
                    person.setMatricule(matricule);
                    person.setNom(nom);
                    person.setPrenom(prenoms);
                    person.setDateNaissance(dateNaissance);
                    personList.add(person);

                    if (personList.size() >= batchSize) {
                        saveBatch(personList);
                    }

                    totalCount++;
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("Erreur ligne " + row.getRowNum() + " : " + e.getMessage());
                }
            }

            // Sauvegarde les dernières données si la liste n'est pas vide
            if (!personList.isEmpty()) {
                saveBatch(personList);
            }

            System.out.println("Importation terminée !");
            System.out.println("Total traitées : " + totalCount);
            System.out.println("Total erreurs : " + errorCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertBatch(List<Object[]> batchArgs) {
        String sql = "INSERT INTO person (matricule, nom, prenom, date_naissance, status) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Transactional
    private void saveBatch(List<Person> personList) {
        try {
            personRepository.saveAll(personList);  // Insertion par lots
            entityManager.flush();
            entityManager.clear();
            personList.clear();  // On vide la liste
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showRecords() {
        List<Person> people = personRepository.findAll();
        System.out.println("Liste des enregistrements:");
        for (Person p : people) {
            System.out.printf("Matricule: %s, Nom: %s, Prénom: %s, Date de naissance: %s, Statut: %s\n",
                    p.getMatricule(), p.getNom(), p.getPrenom(), p.getDateNaissance(), p.getStatus());
        }
    }

    // 🔍 Rechercher par nom (partiel)
    public void searchByNom(String nom) {
        List<Person> people = personRepository.findByNomContainingIgnoreCase(nom);
        if (people.isEmpty()) {
            System.out.println("❌ Aucun résultat trouvé pour le nom : " + nom);
        } else {
            people.forEach(this::printPerson);
        }
    }

    // 🔍 Rechercher par statut
    public void searchByStatus(String status) {
        List<Person> people = personRepository.findByStatus(status);
        if (people.isEmpty()) {
            System.out.println("❌ Aucun résultat trouvé pour le statut : " + status);
        } else {
            people.forEach(this::printPerson);
        }
    }

    public void searchByMatricule(String matricule) {
        List<Person> people = personRepository.findByMatricule(matricule);
        if (people.isEmpty()) {
            System.out.println("❌ Aucun résultat trouvé pour le matricule : " + matricule);
        } else {
            people.forEach(this::printPerson);
        }
    }

    public List<Person> findByDateNaissance(Date dateNaissance) {
        List<Person> people = personRepository.findByDateNaissance(dateNaissance);
        for (Person person : people) {
            printPerson(person);
        }
        return people;
    }

    public List<Person> findBornBefore(Date date) {
        List<Person> people = personRepository.findByDateNaissanceBefore(date);
        for (Person person : people) {
            printPerson(person);
        }
        return people;
    }

    public List<Person> findBornAfter(Date date) {
        List<Person> people = personRepository.findByDateNaissanceAfter(date);
        for (Person person : people) {
            printPerson(person);
        }
        return people;
    }

    public List<Person> findBornBetween(Date startDate, Date endDate) {
        List<Person> people = personRepository.findByDateNaissanceBetween(startDate, endDate);
        for (Person person : people) {
            printPerson(person);
        }
        return people;
    }

    public List<Person> findByAgeGreaterThanEqual(int age) {
        List<Person> allPersons = personRepository.findAll();
        List<Person> result = allPersons.stream()
                .filter(person -> calculateAge(person.getDateNaissance()) >= age)
                .collect(Collectors.toList());
        for (Person person : result) {
            printPerson(person);
        }
        return result;
    }

    public List<Person> findByAgeLessThan(int age) {
        List<Person> allPersons = personRepository.findAll();
        List<Person> result = allPersons.stream()
                .filter(person -> calculateAge(person.getDateNaissance()) < age)
                .collect(Collectors.toList());
        for (Person person : result) {
            printPerson(person);
        }
        return result;
    }

    public List<Person> findByAnniversaireAujourdHui() {
        LocalDate today = LocalDate.now();
        List<Person> allPersons = personRepository.findAll();
        List<Person> result = allPersons.stream()
                .filter(person -> {
                    LocalDate birthDate = new java.sql.Date(person.getDateNaissance().getTime()).toLocalDate();
                    return birthDate.getMonth() == today.getMonth() && birthDate.getDayOfMonth() == today.getDayOfMonth();
                })
                .collect(Collectors.toList());
        for (Person person : result) {
            printPerson(person);
        }
        return result;
    }


    private Date parseDate(String dateStr) throws ParseException {
        String[] formats = {"yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy", "dd-MM-yyyy"};
        for (String format : formats) {
            try {
                return new Date(new SimpleDateFormat(format).parse(dateStr).getTime());
            } catch (ParseException ignored) {}
        }
        throw new ParseException("Format de date inconnu: " + dateStr, 0);
    }

    private String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";  // Si la cellule est vide, renvoie une chaîne vide

        switch (cell.getCellType()) {
            case NUMERIC:
                // Si la cellule est une date, renvoie la date sous forme de texte
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // Date au format texte
                } else {
                    // Si c'est un nombre, on le convertit en chaîne
                    return String.valueOf(cell.getNumericCellValue());
                }
            case STRING:
                return cell.getStringCellValue();  // Si c'est une chaîne, on la retourne directement
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());  // Si c'est une valeur booléenne, on la convertit en chaîne
            default:
                return "";  // Si le type est autre (ex: vide ou inconnu), retourne une chaîne vide
        }
    }

    // Calculer l'âge d'une personne
    private int calculateAge(Date dateNaissance) {
        LocalDate birthDate = new java.sql.Date(dateNaissance.getTime()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    public void printPerson(Person person) {
        System.out.println("📌 ID: " + person.getId());
        System.out.println("🔢 Matricule: " + person.getMatricule());
        System.out.println("📝 Nom: " + person.getNom());
        System.out.println("📝 Prénom: " + person.getPrenom());
        System.out.println("📅 Date de naissance: " + person.getDateNaissance());
        System.out.println("⚡ Statut: " + person.getStatus());
        System.out.println("-------------------------------------------------");
    }

}

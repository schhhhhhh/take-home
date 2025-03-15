package take_home_challenge.asin.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import take_home_challenge.asin.services.PersonService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Component
@Profile("interactive") // Ce code ne s'exécute que si le profil "interactive" est actif
public class InteractiveRunner  implements CommandLineRunner {

    private final PersonService personService;

    public InteractiveRunner(PersonService personService) {
        this.personService = personService;
    }

    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            boolean running = true;

            while (running) {
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1. Importer les données");
                System.out.println("2. Afficher tous les enregistrements");
                System.out.println("3. Rechercher par matricule");
                System.out.println("4. Rechercher par nom");
                System.out.println("5. Rechercher par statut");
                System.out.println("6. Rechercher par date de naissance");
                System.out.println("7. Rechercher les personnes nées avant une date");
                System.out.println("8. Rechercher les personnes nées après une date");
                System.out.println("9. Rechercher les personnes nées entre deux dates");
                System.out.println("10. Rechercher les personnes d'au moins un certain âge");
                System.out.println("11. Rechercher les personnes de moins d’un certain âge");
                System.out.println("12. Rechercher les personnes dont l'anniversaire est aujourd'hui");
                System.out.println("13. Quitter");
                System.out.print("Choisissez une option (1-13) : ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        System.out.print("Entrez le chemin du fichier CSV : ");
                        String filePath = scanner.nextLine().trim();
                        personService.importData(filePath);
                        break;
                    case "2":
                        personService.showRecords();
                        break;
                    case "3":
                        System.out.print("Entrez le matricule : ");
                        String matricule = scanner.nextLine().trim();
                        personService.searchByMatricule(matricule);
                        break;
                    case "4":
                        System.out.print("Entrez un nom ou une partie du nom : ");
                        String nom = scanner.nextLine().trim();
                        personService.searchByNom(nom);
                        break;
                    case "5":
                        System.out.print("Entrez un statut : ");
                        String status = scanner.nextLine().trim();
                        personService.searchByStatus(status);
                        break;
                    case "6":
                        System.out.print("Entrez une date de naissance (format: dd/MM/yyyy) : ");
                        String dateNaissanceStr = scanner.nextLine().trim();
                        try {
                            Date dateNaissance = sdf.parse(dateNaissanceStr);
                            personService.findByDateNaissance(dateNaissance);
                        } catch (Exception e) {
                            System.out.println("❌ Format de date invalide !");
                        }
                        break;
                    case "7":
                        System.out.print("Entrez une date limite (format: dd/MM/yyyy) : ");
                        String beforeDateStr = scanner.nextLine().trim();
                        try {
                            Date beforeDate = sdf.parse(beforeDateStr);
                            personService.findBornBefore(beforeDate);
                        } catch (Exception e) {
                            System.out.println("❌ Format de date invalide !");
                        }
                        break;
                    case "8":
                        System.out.print("Entrez une date limite (format: dd/MM/yyyy) : ");
                        String afterDateStr = scanner.nextLine().trim();
                        try {
                            Date afterDate = sdf.parse(afterDateStr);
                            personService.findBornAfter(afterDate);
                        } catch (Exception e) {
                            System.out.println("❌ Format de date invalide !");
                        }
                        break;
                    case "9":
                        System.out.print("Entrez la date de début (format: dd/MM/yyyy) : ");
                        String startDateStr = scanner.nextLine().trim();
                        System.out.print("Entrez la date de fin (format: dd/MM/yyyy) : ");
                        String endDateStr = scanner.nextLine().trim();
                        try {
                            Date startDate = sdf.parse(startDateStr);
                            Date endDate = sdf.parse(endDateStr);
                            personService.findBornBetween(startDate, endDate);
                        } catch (Exception e) {
                            System.out.println("❌ Format de date invalide !");
                        }
                        break;
                    case "10":
                        System.out.print("Entrez l'âge minimum : ");
                        try {
                            int minAge = Integer.parseInt(scanner.nextLine().trim());
                            personService.findByAgeGreaterThanEqual(minAge);
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Veuillez entrer un âge valide !");
                        }
                        break;
                    case "11":
                        System.out.print("Entrez l'âge maximum : ");
                        try {
                            int maxAge = Integer.parseInt(scanner.nextLine().trim());
                            personService.findByAgeLessThan(maxAge);
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Veuillez entrer un âge valide !");
                        }
                        break;
                    case "12":
                        personService.findByAnniversaireAujourdHui();
                        break;
                    case "13":
                        running = false;
                        System.out.println("👋 Au revoir !");
                        break;
                    default:
                        System.out.println("❌ Option invalide, veuillez réessayer !");
                        break;
                }

                if (running) {
                    System.out.print("Souhaitez-vous effectuer une autre opération ? (oui/non) : ");
                    String continueChoice = scanner.nextLine().trim().toLowerCase();
                    if (!continueChoice.equals("oui")) {
                        running = false;
                        System.out.println("👋 Au revoir !");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Une erreur est survenue : " + e.getMessage());
        } finally {
            scanner.close();
        }
    }



}

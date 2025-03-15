# 🚀 Gestion des Personnes (ASIN)

Un projet **Spring Boot** permettant de gérer une base de données de personnes avec des fonctionnalités avancées de recherche et d'importation de données.

---

## 🔹 Fonctionnalités Principales

✅ **Importation des données** depuis un fichier CSV  
✅ **Gestion et recherche des personnes** par **matricule, nom, statut, date de naissance**  
✅ **Filtrage avancé** :  
   - Personnes nées **avant** une date  
   - Personnes nées **après** une date  
   - Personnes nées **entre** deux dates  
✅ **Calcul de l'âge** et recherche des personnes selon l'âge  
✅ **Détection des anniversaires du jour**  

---

## 🛠️ Technologies Utilisées

- **Java 17**  
- **Spring Boot 3**  
- **Spring Data JPA**  
- **MySQL**  
- **Maven**  
- **Lombok**  

---

## 📥 Installation et Configuration

### 📌 **1️⃣ Prérequis**
Avant d'exécuter l'application, assure-toi d'avoir installé :

- **Java 17** → [Télécharger ici](https://adoptium.net/)  
- **Maven** → [Télécharger ici](https://maven.apache.org/download.cgi)  
- **MySQL** (ou MariaDB)  
- **Un IDE** (IntelliJ, Eclipse, VS Code...)  

---

### 📌 **2️⃣ Configuration de la base de données**
L'application utilise **MySQL**.  
Créer la base de données qui avec le nom asin_people_v1 si vous souhaitez exécuter le jar

1️⃣ Compilation avec Maven
Dans un terminal, exécute :

sh
mvn clean package
Un fichier .jar sera généré dans le dossier target/ :

sh
target/asin-0.0.1-SNAPSHOT.jar
2️⃣ Exécution de l'application
Lance l'application en mode interactif :

sh
java -jar target/asin-0.0.1-SNAPSHOT.jar --spring.profiles.active=interactive
L'application démarre et affiche un menu interactif.

🖥️ Utilisation
Une fois l'application lancée, tu verras un menu affichant les options disponibles :
Importation terminée avec succès !
Souhaitez-vous effectuer une autre opération ? (oui/non) : oui

=== MENU PRINCIPAL ===
1. Importer les données
2. Afficher tous les enregistrements
3. Rechercher par matricule
4. Rechercher par nom
5. Rechercher par statut
6. Rechercher par date de naissance
7. Rechercher les personnes nées avant une date
8. Rechercher les personnes nées après une date
9. Rechercher les personnes nées entre deux dates
10. Rechercher les personnes d'au moins un certain âge
11. Rechercher les personnes de moins d?un certain âge
12. Rechercher les personnes dont l'anniversaire est aujourd'hui
13. Quitter
Choisissez une option (1-13) :


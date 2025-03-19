# Projet MAAR : Un web service RESTful 

## Membres de l'équipe-projet

| Prénom  | NOM       | Username   |
|---------|-----------|------------|
| Alyssia | Leclerc   | Alyssl24   |
| Chedli  | Benjaafar | Chaydonart |

## Instructions

### Repas:

- URL pour le repas : /recipe/meal/{cuisineType}

- Méthode HTTP : GET

- Paramètre : cuisineType une chaîne de caractères indiquant le type de cuisine (ex. "italian", "mexican", etc.)

### Boisson:
  
  - URL pour la boisson : /recipe/drink
  - ou alors : /recipe/drink?alcoholic=true
 
  - Paramètre : alcoholic qui prend en paramètre un boolean indiquant si on veut une boisson alcoolisé ou non. Si le paramètre n'est pas mie, alors une boisson au hasard sera proposé.

Pour faire des tests il vous suffit lancer le serveur avec le Main.java et lancer ensuite une requête de la sorte : 
http://localhost:8000/recipe/meal/<cuisineType>

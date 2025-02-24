# Projet MAAR : Un web service RESTful 

## Membres de l'équipe-projet

| Prénom  | NOM       | Username   |
|---------|-----------|------------|
| Alyssia | Leclerc   | Alyssl24   |
| Chedli  | Benjaafar | Chaydonart |

## Instructions

URL : /recipe/meal/{cuisineType}
Méthode HTTP : GET
Paramètre : cuisineType une chaîne de caractères indiquant le type de cuisine (ex. "italian", "mexican", etc.)

Pour faire des tests il vous suffit lancer le serveur avec le Main.java et lancer ensuite une requête de la sorte : 
http://localhost:8000/recipe/meal/<cuisineType>

## ProTips™ (vous pouvez supprimer ceci ensuite)

- Suivez les instructions de la feuille de TP1.

- Nous imposons d'utiliser `git` avec `ssh` (i.e., pas de Personal Access Token).
  - Vérifiez que vous disposez d'une **clé SSH** (publique → dans https://github.com/settings/keys)
  - Sinon une révision MCDC/DéCo s'impose !!!

- Décidez ensuite quel est le membre qui aura le privilège (a.k.a. la lourde tâche) d'initialiser le squelette de projet :
  - Membre 1 : ne clone pas ce dépôt GitHub (!), crée d'abord le projet Maven, fait `git init`, ajoute ce dépôt avec URL SSH dans le remote `origin`, fait un `git fetch`, puis crée un commit avec `.gitignore` et pousse !
  - Membre 2 : clone le dépôt GitHub rempli, et crée un projet IntelliJ de type Import Maven.

# Projet MAAR : Un web service RESTful 

## Membres de l'équipe-projet

| Prénom  | NOM       | Username   |
|---------|-----------|------------|
| Alyssia | Leclerc   | Alyssl24   |
| Chedli  | Benjaafar | Chaydonart |

---

## Instructions

### Repas:

- URL pour le repas : /recipe/meal/{cuisineType}

- Méthode HTTP : GET

- Paramètre : cuisineType une chaîne de caractères indiquant le type de cuisine (ex. "italian", "mexican", etc.)

### Recette de plat (v2 - JSON)

- **URL** : `/v2/recipe/meal/{cuisineType}`
- **Méthode** : `GET`
- **Paramètre** : `cuisineType` (ex: `italian`, `mexican`, `japanese`, etc.)
- **Retour** : une recette de plat au format JSON, conforme au schéma `RecipeMeal.json`

### Boisson:
  
  - URL pour la boisson : /recipe/drink
  - ou alors : /recipe/drink?alcoholic=true
 
  - Paramètre : alcoholic qui prend en paramètre un boolean indiquant si on veut une boisson alcoolisé ou non. Si le paramètre n'est pas mie, alors une boisson au hasard sera proposé.
    
### Recette de boisson (v2 - JSON)

- **URL** : `/v2/recipe/drink`
- **Méthode** : `GET`
- **Paramètre optionnel** : `alcoholic=true|false`
  - `alcoholic=true` → boisson alcoolisée
  - `alcoholic=false` → boisson sans alcool
  - sans paramètre → boisson aléatoire

### Menu complet personnalisé (v2 - JSON)

- **URL** : `/v2/recipe/menu`
- **Méthode** : `POST`
- **Corps JSON** (exemple valide) :
```json
{
  "cuisineType": "japanese",
  "alcohol": true,
  "requiredIngredient": "ginger",
  "maxPreparationTime": 30,
  "constraints": ["vegan", "gluten-free"]
}
```

Pour faire des tests il vous suffit lancer le serveur avec le Main.java et lancer ensuite une requête de la sorte : 
http://localhost:8000/recipe/meal/<cuisineType>


---

## Génération du fichier OpenAPI

Pour générer le fichier OpenAPI (`openapi.json`), exécutez simplement la commande suivante dans le terminal :
```bash
mvn clean compile
```

Le fichier sera automatiquement généré dans le dossier suivant :
```bash
target/generatedtest/openapi.json
```


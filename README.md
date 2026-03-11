# MAAR Project: A RESTful Web Service

## Project Team Members

| First Name   | Last Name        | Username   |
|---------|-----------|------------|
| Alyssia | Leclerc   | Alyssl24   |
| Chedli  | Benjaafar | Chaydonart |

---

## Instructions

### Meals

- Meal URL: `/recipe/meal/{cuisineType}`
- HTTP Method: `GET`
- Parameter: `cuisineType` — a string indicating the type of cuisine (e.g., `"italian"`, `"mexican"`, etc.)

### Meal Recipe (v2 - JSON)

- **URL**: `/v2/recipe/meal/{cuisineType}`
- **Method**: `GET`
- **Parameter**: `cuisineType` (e.g., `italian`, `mexican`, `japanese`, etc.)
- **Response**: a meal recipe in JSON format, following the `RecipeMeal.json` schema

### Drinks

- Drink URL: `/recipe/drink`
- Or: `/recipe/drink?alcoholic=true`

- Parameter: `alcoholic` — a boolean indicating whether the drink should contain alcohol or not.  
  If the parameter is not provided, a random drink will be returned.
    
### Drink Recipe (v2 - JSON)

- **URL**: `/v2/recipe/drink`
- **Method**: `GET`
- **Optional Parameter**: `alcoholic=true|false`
  - `alcoholic=true` → alcoholic drink
  - `alcoholic=false` → non-alcoholic drink
  - no parameter → random drink

### Personalized Full Menu (v2 - JSON)

- **URL** : `/v2/recipe/menu`
- **Method** : `POST`
- **JSON Body** (valid example) :
```json
{
  "cuisineType": "japanese",
  "alcohol": true,
  "requiredIngredient": "ginger",
  "maxPreparationTime": 30,
  "constraints": ["vegan", "gluten-free"]
}
```

To run tests, simply start the server using `Main.java` and then send a request such as:  
http://localhost:8000/recipe/meal/<cuisineType>


---

## OpenAPI File Generation

To generate the OpenAPI file (`openapi.json`), simply run the following command in the terminal:
```bash
mvn clean compile
```

The file will automatically be generated in the following directory:
```bash
target/generatedtest/openapi.json
```


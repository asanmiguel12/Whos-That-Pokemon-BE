# Whos-That-Pokemon-BE

Whos-That-Pokemon-BE is a backend service for a Pokémon guessing game. It serves Pokémon logic and user input to a frontend client.

## Features
- Serves Pokémon images and data
- RESTful API endpoints
- Built with Java and Spring Boot

## Getting Started

### Prerequisites
- Java 17 or higher
- Gradle (or use the provided wrapper)

### Installation
1. Clone the repository:
   ```sh
   git clone <repository-url>
   cd whosthatpokemon-be
   ```
2. Build the project:
   ```sh
   ./gradlew build
   ```
3. Run the application:
   ```sh
   ./gradlew bootRun
   ```

## Usage
The backend will start on `http://localhost:8082` by default. API endpoints can be accessed to retrieve Pokémon data and images.

## Project Structure
- `src/main/java` - Java source code
- `src/main/resources/images/pokemonImages` - Pokémon images
- `src/main/resources/application.properties` - Application configuration


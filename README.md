# Centro Fitness API

Backend REST per la gestione di un centro fitness sviluppato con Java e Spring Boot.

Il progetto implementa una API per amministrare clienti, istruttori, corsi, lezioni, sale e abbonamenti. Include autenticazione JWT, refresh token, ruoli applicativi e documentazione OpenAPI/Swagger.

## Obiettivo del progetto

L'obiettivo e' costruire un backend strutturato per un dominio realistico, separando controller, service, repository/DAO, DTO ed entity. Il progetto e' pensato come base backend per una possibile applicazione gestionale dedicata a palestre e centri fitness.

## Funzionalita principali

- Registrazione e login utenti
- Autenticazione con access token JWT
- Gestione refresh token
- Ruoli applicativi per clienti, istruttori e amministratori
- Cambio password e reset password amministrativo
- Gestione clienti e istruttori
- Gestione corsi, lezioni e sale
- Gestione abbonamenti e stato della sottoscrizione
- Validazione input tramite DTO
- Gestione centralizzata delle eccezioni
- Documentazione API tramite Swagger/OpenAPI

## Stack tecnico

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA / Hibernate
- MySQL
- JWT
- Lombok
- ModelMapper
- OpenAPI / Swagger UI
- Maven

## Struttura principale

```text
src/main/java/com/itscadmo/centro_fitness/
+-- Controller/
+-- Data/
|   +-- DAO/
|   +-- Entity/
|   +-- Service/
+-- DTO/
+-- Security/
+-- Config/
```

## Configurazione

Le configurazioni sensibili sono lette da variabili ambiente. Il file `.env.example` mostra i valori necessari per un ambiente locale.

Variabili principali:

```env
DB_URL=jdbc:mysql://localhost:3306/centro_fitness?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=replace-with-local-db-password
JWT_SECRET=replace-with-a-long-random-secret-at-least-32-characters
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000
CORS_ALLOWED_ORIGINS=http://localhost:4200
ADMIN_BOOTSTRAP_ENABLED=false
```

Nota: i valori di default presenti in `application.properties` servono solo per sviluppo locale. In un ambiente reale `JWT_SECRET`, credenziali database e CORS devono essere configurati tramite variabili ambiente o secret manager.

Il bootstrap dell'utente admin e' disattivato di default. Se serve creare un admin locale iniziale, abilita `ADMIN_BOOTSTRAP_ENABLED=true` e imposta una password tramite `ADMIN_PASSWORD`.

## Avvio locale

1. Crea un database MySQL chiamato `centro_fitness`.

2. Configura le variabili ambiente necessarie, usando `.env.example` come riferimento.

3. Avvia l'applicazione con Maven.

```bash
./mvnw spring-boot:run
```

Su Windows:

```bash
mvnw.cmd spring-boot:run
```

L'applicazione parte di default su:

```text
http://localhost:8080
```

## Documentazione API

Con l'applicazione avviata, Swagger UI e' disponibile su:

```text
http://localhost:8080/swagger-ui/index.html
```

Gli endpoint di autenticazione sono sotto:

```text
/api/auth
```

Esempi:

- `POST /api/auth/registra`
- `POST /api/auth/accedi`
- `POST /api/auth/rinnova`
- `POST /api/auth/logout`
- `POST /api/auth/cambia-password`

## Sicurezza

Il progetto usa Spring Security con sessioni stateless e autenticazione JWT.

Le password utente vengono salvate usando hashing tramite `PasswordEncoder`/BCrypt. I token JWT e le credenziali database non devono essere hardcoded in produzione: vanno configurati tramite variabili ambiente.

La creazione automatica dell'admin iniziale e' disattivata di default per evitare credenziali note nel codice sorgente.

## Stato del progetto

Il progetto e' un backend MVP in evoluzione. La struttura principale e' presente, ma alcune aree possono essere migliorate per renderlo piu' vicino a uno standard production-ready:

- test piu' completi su service e controller
- profili separati per `dev`, `test` e `prod`
- inizializzazione database tramite migration tool come Flyway o Liquibase
- gestione piu' rigorosa degli errori API
- dockerizzazione dell'applicazione e del database
- documentazione di esempi request/response

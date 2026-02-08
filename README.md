PW05 - JDBC (Data Access Object)
Student Information
Name: Mahmoud El-Sayed
Practical Work: PW05 (JDBC Implementation)
Repository: pw05-JDBC

Project Description
This project demonstrates the implementation of the DAO (Data Access Object) pattern using JDBC to interact with a SQLite database.
It handles persistence for a movie catalog system.

Key Features:
Database: File-based SQLite (auto-generated during tests).
Resource Management: Implements try-with-resources for automatic closing of JDBC connections and statements.

Entities:
Genre (ID, Name)
Movie (ID, Title, Release Date, Genre ID)

Requirements
JDK: 21 or higher
Build Tool: Maven 3.x+
Database: SQLite (Driver included via Maven dependencies)

How to Run
To run the automated test suite and verify the DAO implementations, use the following command:

Bash
mvn test

Implementation Details

GenreDao
listGenres(): Retrieves all genres from the database.
addGenre(String name): Persists a new genre.
getGenre(String name): Returns an Optional<Genre> (Bonus Stage 2 implementation).

MovieDao
listMovies(): Retrieves the full movie catalog.
listMoviesByGenre(String genreName): Filters movies by joining with the Genre table.
addMovie(Movie movie): Inserts a movie and returns the object populated with the generated ID.

Notes
Testing: The schema is recreated before each test run to ensure a clean state (GenreDaoTestCase and MovieDaoTestCase).
Git: The SQLite database file (.db) is ignored by version control to prevent local data conflicts.

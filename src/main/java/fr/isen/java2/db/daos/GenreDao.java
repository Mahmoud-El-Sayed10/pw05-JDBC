package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.isen.java2.db.entities.Genre;

/**
 * <p>
 * This class provides JDBC-based operations for the {@code genre} SQL table.
 * Connections are obtained through {@link DataSourceFactory}.
 * </p>
 */
public class GenreDao {

	/**
	 * Retrieves all genres from the database.
	 *
	 * @return a list containing all {@link Genre} rows from the {@code genre} table
	 * @throws RuntimeException if a database access error occurs
	 */
	public List<Genre> listGenres() {
		String sql = "SELECT * FROM genre";
		List<Genre> genres = new ArrayList<>();

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				int id = resultSet.getInt("idgenre");
				String name = resultSet.getString("name");
				genres.add(new Genre(id, name));
			}

			return genres;
		} catch (SQLException e) {
			throw new RuntimeException("Error while listing genres", e);
		}
	}

	/**
	 * Retrieves a genre by its name.
	 *
	 * @param name the genre name to look up
	 * @return an {@link Optional} containing the matching {@link Genre} if found,
	 *         otherwise {@link Optional#empty()}
	 * @throws RuntimeException if a database access error occurs
	 */
	public Optional<Genre> getGenre(String name) {
		String sql = "SELECT * FROM genre WHERE name = ?";

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, name);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					int id = resultSet.getInt("idgenre");
					String genreName = resultSet.getString("name");
					return Optional.of(new Genre(id, genreName));
				}
				return Optional.empty(); 
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error while getting genre by name: " + name, e);
		}
	}

	/**
	 * Inserts a new genre into the database.
	 *
	 * @param name the name of the genre to insert
	 * @throws RuntimeException if a database access error occurs
	 */
	public void addGenre(String name) {
		String sql = "INSERT INTO genre(name) VALUES(?)";

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, name);
			statement.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException("Error while adding genre: " + name, e);
		}
	}
}

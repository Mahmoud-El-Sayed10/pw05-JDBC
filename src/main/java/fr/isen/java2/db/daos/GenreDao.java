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

public class GenreDao {

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

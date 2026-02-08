package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

/**
 * <p>
 * This class provides JDBC-based operations for the {@code movie} SQL table.
 * Movie rows are retrieved together with their associated {@link Genre} using
 * SQL JOIN queries. Connections are obtained through {@link DataSourceFactory}.
 * </p>
 */
public class MovieDao {

	/**
	 * Retrieves all movies from the database, including their associated genre.
	 *
	 * @return a list containing all {@link Movie} rows joined with their {@link Genre}
	 * @throws RuntimeException if a database access error occurs
	 */
	public List<Movie> listMovies() {
		String sql = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre";
		List<Movie> movies = new ArrayList<>();

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				Integer id = resultSet.getInt("idmovie");
				String title = resultSet.getString("title");

				Timestamp ts = resultSet.getTimestamp("release_date");
				LocalDate releaseDate = null;
				if (ts != null) {
					releaseDate = ts.toLocalDateTime().toLocalDate();
				}

				Integer duration = (Integer) resultSet.getObject("duration");
				String director = resultSet.getString("director");
				String summary = resultSet.getString("summary");

				Integer genreId = resultSet.getInt("idgenre");
				String genreName = resultSet.getString("name");
				Genre genre = new Genre(genreId, genreName);

				movies.add(new Movie(id, title, releaseDate, genre, duration, director, summary));
			}

			return movies;

		} catch (SQLException e) {
			throw new RuntimeException("Error while listing movies", e);
		}
	}

	/**
	 * Retrieves all movies that belong to a specific genre (by genre name).
	 *
	 * @param genreName the genre name used to filter movies
	 * @return a list of {@link Movie} objects whose {@link Genre} name matches {@code genreName}
	 * @throws RuntimeException if a database access error occurs
	 */
	public List<Movie> listMoviesByGenre(String genreName) {
		String sql = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?";
		List<Movie> movies = new ArrayList<>();

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, genreName);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Integer id = resultSet.getInt("idmovie");
					String title = resultSet.getString("title");

					Timestamp ts = resultSet.getTimestamp("release_date");
					LocalDate releaseDate = null;
					if (ts != null) {
						releaseDate = ts.toLocalDateTime().toLocalDate();
					}

					Integer duration = (Integer) resultSet.getObject("duration");
					String director = resultSet.getString("director");
					String summary = resultSet.getString("summary");

					Integer genreId = resultSet.getInt("idgenre");
					String gName = resultSet.getString("name");
					Genre genre = new Genre(genreId, gName);

					movies.add(new Movie(id, title, releaseDate, genre, duration, director, summary));
				}
			}

			return movies;

		} catch (SQLException e) {
			throw new RuntimeException("Error while listing movies by genre: " + genreName, e);
		}
	}

	/**
	 * Inserts a new movie into the database and returns a {@link Movie} object
	 * containing the generated database id.
	 *
	 * @param movie the {@link Movie} to insert (without an id)
	 * @return a new {@link Movie} instance containing the generated id
	 * @throws RuntimeException if a database access error occurs or if no generated id is returned
	 */
	public Movie addMovie(Movie movie) {
		String sql = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, movie.getTitle());

			LocalDate releaseDate = movie.getReleaseDate();
			if (releaseDate == null) {
				statement.setTimestamp(2, null);
			} else {
				statement.setTimestamp(2, Timestamp.valueOf(releaseDate.atStartOfDay()));
			}

			statement.setInt(3, movie.getGenre().getId());

			if (movie.getDuration() == null) {
				statement.setObject(4, null);
			} else {
				statement.setInt(4, movie.getDuration());
			}

			statement.setString(5, movie.getDirector());
			statement.setString(6, movie.getSummary());

			statement.executeUpdate();

			ResultSet keys = statement.getGeneratedKeys();
			if (keys.next()) {
				int id = keys.getInt(1);
				keys.close();
				return new Movie(id, movie.getTitle(), movie.getReleaseDate(), movie.getGenre(), movie.getDuration(),
						movie.getDirector(), movie.getSummary());
			}
			keys.close();

			throw new RuntimeException("No generated key returned for movie insert");

		} catch (SQLException e) {
			throw new RuntimeException("Error while adding movie: " + movie.getTitle(), e);
		}
	}
}

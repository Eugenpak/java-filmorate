package ru.yandex.practicum.filmorate.storage.like;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.PopularFilm;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({LikeDaoImpl.class, PopularFilmRowMapper.class})
class LikeDaoImplTest {
    @Autowired
    private LikeDao likeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("DELETE FROM film_directors");
        jdbcTemplate.execute("DELETE FROM film_mpas");
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM genres");
        jdbcTemplate.execute("DELETE FROM users");
    }

    private void setFilm(long filmId, String name, LocalDate releaseDate) {
        jdbcTemplate.update(
                "INSERT INTO films (id, name, description, release_date, duration) VALUES (?, ?, ?, ?, ?)",
                filmId, name, "Test film", releaseDate, 90
        );
    }

    private void setUser(long userId, String email) {
        jdbcTemplate.update(
                "INSERT INTO users (id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                userId, email, "login" + userId, "User " + userId, LocalDate.of(1990, 1, 1)
        );
    }

    private void setGenre(long genreId, String name) {
        jdbcTemplate.update(
                "INSERT INTO genres (id, name) VALUES (?, ?)",
                genreId, name
        );
    }

    private void setFilmGenre(long filmId, long genreId) {
        jdbcTemplate.update(
                "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                filmId, genreId
        );
    }

    @Test
    public void testAddLikeAndGetLikedFilmsIdsByUser() {
        setFilm(1L, "Film", LocalDate.of(2001, 7, 8));
        setUser(1L, "user@example.com");

        Set<Long> likedFilmsBefore = likeDao.getLikedFilmsIdsByUser(1L);
        assertThat(likedFilmsBefore).isEmpty();

        likeDao.add(1L, 1L);

        Set<Long> likedFilmsAfter = likeDao.getLikedFilmsIdsByUser(1L);
        assertThat(likedFilmsAfter).containsExactly(1L);
    }

    @Test
    public void testDeleteLike() {
        setFilm(1L, "Film", LocalDate.of(2001, 7, 8));
        setUser(1L, "user@example.com");

        likeDao.add(1L, 1L);

        Set<Long> likedFilms = likeDao.getLikedFilmsIdsByUser(1L);
        assertThat(likedFilms).contains(1L);

        likeDao.delete(1L, 1L);
        Set<Long> likedFilmsAfter = likeDao.getLikedFilmsIdsByUser(1L);
        assertThat(likedFilmsAfter).doesNotContain(1L);
    }

    @Test
    public void testFindPopularFilmsId() {
        setFilm(1L, "Film 1", LocalDate.of(2022, 1, 1));
        setFilm(2L, "Film 2", LocalDate.of(2022, 1, 1));
        setUser(1L, "user1@example.com");
        setUser(2L, "user2@example.com");

        likeDao.add(1L, 1L);
        likeDao.add(1L, 2L);
        likeDao.add(2L, 1L);

        Collection<PopularFilm> popularFilms = likeDao.findPopularFilmsId(10);
        List<Long> popularFilmIds = popularFilms.stream()
                .map(popFilm -> popFilm.getFilmId())
                .collect(Collectors.toList());

        assertThat(popularFilmIds).containsExactly(1L, 2L);
    }

    @Test
    public void testDeleteAllPopularFilms() {
        setFilm(1L, "Film", LocalDate.of(2001, 7, 8));
        setUser(1L, "user@example.com");

        likeDao.add(1L, 1L);

        Set<Long> likedFilmsBefore = likeDao.getLikedFilmsIdsByUser(1L);
        assertThat(likedFilmsBefore).isNotEmpty();

        likeDao.deleteAllPopularFilms();
        Set<Long> likedFilmsAfter = likeDao.getLikedFilmsIdsByUser(1L);
        assertThat(likedFilmsAfter).isEmpty();
    }

    @Test
    public void testFindPopularFilmsByGenreYear() {
        setFilm(1L, "Genre Film", LocalDate.of(2001, 7, 8));
        setGenre(1L, "Comedy");
        setFilmGenre(1L, 1L);
        setUser(1L, "user@example.com");
        likeDao.add(1L, 1L);

        List<Long> popularFilms = likeDao.findPopularFilmsByGenreYear(10, 1L, 2001);
        assertThat(popularFilms).hasSize(1).containsExactly(1L);
    }

    @Test
    public void testGetUserIdsByLikedFilm() {
        setFilm(1L, "Film", LocalDate.of(2022, 1, 1));
        setUser(1L, "user1@example.com");
        setUser(2L, "user2@example.com");

        likeDao.add(1L, 1L);
        likeDao.add(1L, 2L);

        Set<Long> userIds = likeDao.getUserIdsByLikedFilm(1L);
        assertThat(userIds).containsExactlyInAnyOrder(1L, 2L);
    }
}

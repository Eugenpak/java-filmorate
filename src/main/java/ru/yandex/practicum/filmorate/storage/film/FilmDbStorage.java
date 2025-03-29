package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.filmmpa.FilmMpaDao;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Qualifier("FilmDbStorage")
@Slf4j
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name,description,release_date,duration) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?" +
            " WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private final FilmMpaDao filmMpaDao;
    private final FilmGenreDao filmGenreDao;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeDao likeDao;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper,FilmMpaDao filmMpaDao,
                         FilmGenreDao filmGenreDao,MpaStorage mpaStorage,
                         GenreStorage genreStorage,LikeDao likeDao) {
        super(jdbc, mapper, Film.class);
        this.filmMpaDao = filmMpaDao;
        this.filmGenreDao = filmGenreDao;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likeDao = likeDao;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("FilmDbStorage start findAll()");
        Collection<Film> listFilm = findMany(FIND_ALL_QUERY);
        for (Film el : listFilm) {
            el.setGenres(filmGenreDao.findGenresById(el.getId()));
            Optional<Mpa> mpaIdOpt = filmMpaDao.get(el.getId());
            if (mpaIdOpt.isPresent()) {
                el.setMpa(mpaIdOpt.get());
            } else {
                el.setMpa(null);
            }
        }
        log.info("FilmDbStorage end findAll()");
        return listFilm;
    }

    @Override
    public Film create(Film film) {
        log.info("FilmDbStorage start create()");
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
        film.setId(id);
        if (film.getMpa() != null) {
            if (mpaStorage.findMpaById(film.getMpa().getId()).isEmpty()) {
                throw new NotFoundException("Рейтинг mpa: {id:" + film.getMpa().getId() + "} не найден");
            }
            filmMpaDao.add(film.getId(), film.getMpa().getId());
            log.info("FilmDbStorage create(), добавлен Mpa");
        }
        if (film.getGenres().size() != 0) {
            genreStorage.findNotValid(film.getGenres());
            filmGenreDao.addSet(film.getId(), film.getGenres());
            log.info("FilmDbStorage create(), добавлен genres");
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("FilmDbStorage start update()");
        long mpaId = film.getMpa().getId();
        Set<Genre> genres = film.getGenres();
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        film.setGenres(filmGenreDao.updateFilmGenres(film.getId(),genres));
        film.setMpa(filmMpaDao.updateFilmMpa(film.getId(),mpaId));
        return film;
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        log.info("FilmDbStorage start findFilmById({}})",id);
        Optional<Film> filmOpt = findOne(FIND_BY_ID_QUERY, id);
        if (filmOpt.isEmpty()) {
            return Optional.empty();
        }
        Film findFilm = filmOpt.get();
        Optional<Mpa> mpaId = filmMpaDao.get(findFilm.getId());
        Set<Genre> genres = filmGenreDao.findGenresById(findFilm.getId());

        if (mpaId.isPresent()) {
            //System.out.println("|-- FilmDbS-findFilmById " + mpaId.get());
            log.info("FilmDbStorage findFilmById({}}), добавлен mpa",id);
            findFilm.setMpa(mpaId.get());
        }

        findFilm.setGenres(genres);
        //System.out.println(findFilm);
        log.info("FilmDbStorage findFilmById({}}), добавлен genres",id);
        return Optional.of(findFilm);
    }

    @Override
    public void addLike(long filmId, long userId) {
        log.info("FilmDbStorage start addLike(filmId:{},userId{})",filmId,userId);
        likeDao.add(filmId,userId);
        log.info("FilmDbStorage end addLike(filmId:{},userId{})",filmId,userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.info("FilmDbStorage start deleteLike(filmId:{},userId{})",filmId,userId);
        likeDao.delete(filmId,userId);
        log.info("FilmDbStorage end deleteLike(filmId:{},userId{})",filmId,userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.info("FilmDbStorage start getPopularFilms()");
        return likeDao.findPopularFilmsId(count).stream()
                .map(p -> findFilmById(p.getFilmId()).get())
                .collect(Collectors.toList());
    }
}

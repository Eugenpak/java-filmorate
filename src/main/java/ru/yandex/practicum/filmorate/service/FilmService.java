package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.filmmpa.FilmMpaDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final FilmGenreDao filmGenreDao;
    private final FilmMpaDao filmMpaDao;
    private final LikeDao likeDao;

    private static final LocalDate MY_CONSTANT = LocalDate.of(1895,12,28);

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       UserService userService,
                       FilmGenreDao filmGenreDao,
                       FilmMpaDao filmMpaDao,
                       LikeDao likeDao,
                       GenreService genreService,
                       MpaService mpaService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmGenreDao = filmGenreDao;
        this.filmMpaDao = filmMpaDao;
        this.likeDao = likeDao;
        this.genreService = genreService;
        this.mpaService = mpaService;
    }

    public Collection<Film> findAll() {
        log.info("Start Film findAll()");
        Collection<Film> listFilm = filmStorage.findAll();
        return getFieldsFilm(listFilm);
    }

    private Collection<Film> getFieldsFilm(Collection<Film> listFilm) {
        log.info("F-S getFieldsFilm: begin");
        List<Long> filmIdList = listFilm.stream().map(Film::getId).toList();
        Map<Long,Set<Genre>> dtoGegres = getManyGenreForFilm(filmIdList);
        Map<Long,Mpa> dtoMpa = getManyMpaForFilm(filmIdList);
        Collection<Film> temp = joinFilmGenre(listFilm,dtoGegres);
        log.info("F-S getFieldsFilm: end");
        return joinFilmMpa(temp,dtoMpa);
    }

    private Collection<Film> joinFilmGenre(Collection<Film> listFilm,Map<Long,Set<Genre>> dtoGegres) {
        log.info("F-S joinFilmMpa: {}",dtoGegres);
        Collection<Film> fc = listFilm.stream()
                .peek(f -> {
                    Optional<Set<Genre>> genreOpt = Optional.ofNullable(dtoGegres.get(f.getId()));
                    genreOpt.ifPresent(f::setGenres);
                })
                .collect(Collectors.toList());
        log.info("F-S joinFilmGenre: |-> {}",fc);
        return fc;
    }

    private Collection<Film> joinFilmMpa(Collection<Film> listFilm,Map<Long,Mpa> dtoMpa) {
        log.info("F-S joinFilmMpa: {}",dtoMpa);
        Collection<Film> fc = listFilm.stream()
                .peek(f -> {
                    Optional<Mpa> mpaOpt = Optional.ofNullable(dtoMpa.get(f.getId()));
                    if (mpaOpt.isPresent()) {
                        f.setMpa(mpaOpt.get());
                    } else {
                        f.setMpa(null);
                    }
                })
                .collect(Collectors.toList());
        log.info("F-S joinFilmMpa: |-> {}",fc);
        return fc;
    }

    private Map<Long,Set<Genre>> getManyGenreForFilm(List<Long> filmIdList) {
        log.info("F-S getManyGenreForFilm: {}",filmIdList);
        List<FilmGenre> filmGenreList = filmGenreDao.getFilmGenreByFilmId(filmIdList);
        List<Long> gIdL = filmGenreList.stream().map(FilmGenre::getGenreId).distinct().toList();

        Map<Long, Genre> gl = genreService.getGenreById(gIdL);//-GM
        Map<Long,Set<Genre>> mapFilmGenre = new HashMap<>();//-GM
        filmGenreList.forEach(fg -> { //-GM
            mapFilmGenre.putIfAbsent(fg.getFilmId(), new HashSet<>()); //-GM
            mapFilmGenre.get(fg.getFilmId()).add(gl.get(fg.getGenreId())); //-GM
        });  //-GM
        return mapFilmGenre;
    }

    private Map<Long,Mpa> getManyMpaForFilm(List<Long> filmIdList) {
        log.info("F-S getManyMpaForFilm: {}",filmIdList);
        List<FilmMpa> filmMpaList = filmMpaDao.getFilmMpaByFilmId(filmIdList); //-MM
        List<Long> mIdL = filmMpaList.stream().map(FilmMpa::getMpaId).distinct().toList(); //-MM

        Map<Long, Mpa> mpaM = mpaService.getMpaById(mIdL); //-MM
        Map<Long,Mpa> mapFilmMpa = new HashMap<>(); //-MM
        filmMpaList.forEach(fm -> {  //-MM
            mapFilmMpa.put(fm.getFilmId(), mpaM.get(fm.getMpaId()));  //-MM
        });  //-MM
        return mapFilmMpa;
    }


    public Film create(Film film) {
        // проверяем выполнение необходимых условий
        log.info("Start Film create()");
        log.info("POST->Body Film = " + film);

        if (film.getReleaseDate().isBefore(MY_CONSTANT)) {
            log.error("ValidationException releaseDate");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        // сохраняем новую публикацию в памяти приложения
        film = filmStorage.create(film);
        //---------------------------------------------------------------
        if (film.getMpa() != null) {
            Long mpaId = mpaService.findMpaById(film.getMpa().getId()).getId();
            filmMpaDao.add(film.getId(),mpaId);
            log.info("F-S create(), добавлен Mpa");
        }
        if (film.getGenres().size() != 0) {
            genreService.findNotValid(film.getGenres());
            filmGenreDao.addSet(film.getId(), film.getGenres());
            log.info("F-S create(), добавлен genres");
        }
        //---------------------------------------------------------------
        log.info("Новый фильм сохраняется (id=" + film.getId() + ", name='" + film.getName() + "')");
        return film;
    }

    public Film update(Film newFilm) {
        // проверяем необходимые условия
        log.info("Start Film update()");
        log.info("PUT->Body Film = " + newFilm);
        if (newFilm.getId() == null) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан");
        }
        if (newFilm.getReleaseDate().isBefore(MY_CONSTANT)) {
            log.warn("ValidationException releaseDate");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        Film oldFilm = findFilmById(newFilm.getId());
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        // -> задача! Для оптимизации
        oldFilm.setGenres(newFilm.getGenres());
        oldFilm.setMpa(newFilm.getMpa());
        // если film найдена и все условия соблюдены, обновляем её содержимое
        log.info("Данные фильма обновляются (id=" + oldFilm.getId() + ", name='" + oldFilm.getName() + "')");
        Optional<Mpa> mpaOpt = Optional.ofNullable(oldFilm.getMpa());
        Set<Genre> genres = oldFilm.getGenres();
        Film filmB = filmStorage.update(oldFilm);
        //------------------------------------------------
        filmB.setGenres(filmGenreDao.updateFilmGenres(filmB.getId(),genres));
        mpaOpt.ifPresent(mpa -> filmB.setMpa(filmMpaDao.updateFilmMpa(filmB.getId(), mpa.getId())));
        return filmB;
    }

    public Film findFilmById(long id) {
        Optional<Film> findFilmOpt = filmStorage.findFilmById(id);
        if (findFilmOpt.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        //---------------------------------------------
        Film findFilm = findFilmOpt.get();
        Optional<Mpa> mpaId = filmMpaDao.get(findFilm.getId());
        Set<Genre> genres = filmGenreDao.findGenresById(findFilm.getId());

        if (mpaId.isPresent()) {
            log.info("F-S findFilmById({}}), добавлен mpa",id);
            findFilm.setMpa(mpaId.get());
        }

        findFilm.setGenres(genres);
        log.info("FilmDbStorage findFilmById({}}), добавлен genres",id);
        //---------------------------------------------
        return findFilm;
    }

    public void addLike(long filmId, long userId) {
        log.info("Start FS addLike(filmId={}, userId={})",filmId,userId);
        findFilmById(filmId);
        userService.findUserById(userId);
        try {

            likeDao.add(filmId,userId);
        } catch (Exception ex) {
            String msg = "Пользователь с userId=" + userId +
                    " поставил лайк к фильму с filmId=" + filmId;
            log.info(msg);
            throw new ValidationException(msg);
        }
    }

    public void deleteLike(long filmId, long userId) {
        log.info("Start FS deleteLike(filmId={}, userId={})",filmId,userId);
        findFilmById(filmId);
        userService.findUserById(userId);

        likeDao.delete(filmId,userId);
        log.info("Пользователь с userId=" + userId +
                " удалил лайк к фильму с filmId=" + filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        log.info("Start FS getPopularFilms()");
        Collection<Film> popFilm = likeDao.findPopularFilmsId(count).stream()
                .map(p -> filmStorage.findFilmById(p.getFilmId()).get())
                .collect(Collectors.toList());
        return getFieldsFilm(popFilm);

    }
}

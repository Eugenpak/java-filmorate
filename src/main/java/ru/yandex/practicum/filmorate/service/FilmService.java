package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorDao;
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
    private final DirectorService directorService;
    private final FilmDirectorDao filmDirectorDao;

    private static final LocalDate MY_CONSTANT = LocalDate.of(1895,12,28);

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       UserService userService,
                       FilmGenreDao filmGenreDao,
                       FilmMpaDao filmMpaDao,
                       LikeDao likeDao,
                       GenreService genreService,
                       MpaService mpaService,
                       DirectorService directorService,
                       FilmDirectorDao filmDirectorDao) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmGenreDao = filmGenreDao;
        this.filmMpaDao = filmMpaDao;
        this.likeDao = likeDao;
        this.genreService = genreService;
        this.mpaService = mpaService;
        this.directorService = directorService;
        this.filmDirectorDao = filmDirectorDao;
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
        Map<Long,Collection<Director>> dtoDirector = getManyDirectorForFilm(filmIdList);
        Collection<Film> temp = joinFilmGenre(listFilm,dtoGegres);
        temp = joinFilmDirector(temp,dtoDirector);
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

    private Collection<Film> joinFilmDirector(Collection<Film> listFilm,Map<Long,Collection<Director>> dtoDirectors) {
        log.info("F-S joinFilmDirector: {}",dtoDirectors);
        Collection<Film> fc = listFilm.stream()
                .peek(f -> {
                    Optional<Collection<Director>> directorOpt = Optional.ofNullable(dtoDirectors.get(f.getId()));
                    directorOpt.ifPresent(f::setDirectors);
                })
                .collect(Collectors.toList());
        log.info("F-S joinFilmDirector: |-> {}",fc);
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

    private Map<Long,Collection<Director>> getManyDirectorForFilm(List<Long> filmIdList) {
        log.info("F-S getManyDirectorForFilm: {}",filmIdList);
        List<FilmDirector> filmDirectorList = filmDirectorDao.getFilmDirectorByFilmId(filmIdList);
        List<Long> dIdL = filmDirectorList.stream().map(FilmDirector::getDirectorId).distinct().toList();

        Map<Long, Director> directorM = directorService.getDirectorByListId(dIdL);
        Map<Long,Collection<Director>> mapFilmDirector = new HashMap<>();
        filmDirectorList.forEach(fd -> {
            mapFilmDirector.putIfAbsent(fd.getFilmId(), new HashSet<>());
            mapFilmDirector.get(fd.getFilmId()).add(directorM.get(fd.getDirectorId()));
        });
        return mapFilmDirector;
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
        Set<Genre> sg = film.getGenres();
        //---------------------------------------------------------------
        if (film.getMpa() != null) {
            Long mpaId = mpaService.findMpaById(film.getMpa().getId()).getId();
            filmMpaDao.add(film.getId(),mpaId);
            log.info("F-S create(), добавлен Mpa");
        }
        //---------------------------------------------------------------
        if (film.getGenres().size() != 0) {
            genreService.findNotValid(film.getGenres());
            filmGenreDao.addSet(film.getId(), film.getGenres());
            log.info("F-S create(), добавлен genres");
        }
        //---------------------------------------------------------------
        if (film.getDirectors().size() != 0) {
            Collection<Long> directorsId = film.getDirectors().stream().map(Director::getId).toList();
            directorService.findNotValid(directorsId);
            log.info("F-S create()->directors, start filmDirectorDao.addSet()");
            filmDirectorDao.addSet(film.getId(), film.getDirectors());
            log.info("F-S create(), добавлен directors");
        }
        //---------------------------------------------------------------
        film = getFieldsFilm(List.of(film)).stream().toList().get(0);
        Set<Genre> temp = film.getGenres();
        for (Genre el : sg) {
            Optional<Genre> ft = temp.stream().filter(t -> t.getId().equals(el.getId())).findFirst();
            if (ft.isPresent()) {
                el.setName(ft.get().getName());
            }
        }
        film.setGenres(sg);
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
        oldFilm.setDirectors(newFilm.getDirectors());
        // если film найден и все условия соблюдены, обновляем её содержимое
        log.info("Данные фильма обновляются (id=" + oldFilm.getId() + ", name='" + oldFilm.getName() + "')");
        Optional<Mpa> mpaOpt = Optional.ofNullable(oldFilm.getMpa());
        Set<Genre> genres = oldFilm.getGenres();
        Film filmB = filmStorage.update(oldFilm);
        //------------------------------------------------
        filmB.setGenres(filmGenreDao.updateFilmGenres(filmB.getId(),genres));
        mpaOpt.ifPresent(mpa -> filmB.setMpa(filmMpaDao.updateFilmMpa(filmB.getId(), mpa.getId())));
        filmB.setDirectors(updateFilmDirectors(filmB.getId(),filmB.getDirectors()));
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
        Collection<Director> directors = findDirectorsByFilmId(findFilm.getId());

        if (mpaId.isPresent()) {
            log.info("F-S findFilmById({}}), добавлен mpa",id);
            findFilm.setMpa(mpaId.get());
        }

        findFilm.setGenres(genres);
        findFilm.setDirectors(directors);
        log.info("FilmDbStorage findFilmById({}}), добавлен genres",id);
        //---------------------------------------------
        return findFilm;
    }

    public Collection<Director> findDirectorsByFilmId(Long filmId) {
        List<FilmDirector> fdl = filmDirectorDao.getFilmDirectorByFilmId(List.of(filmId));
        List<Long> dIdL = fdl.stream().map(FilmDirector::getDirectorId).toList();

        return directorService.getDirectorByListId(dIdL).values();
    }

    public Collection<Director> updateFilmDirectors(long filmId, Collection<Director> directors) {
        log.debug("FilmService updateFilmDirectors({}, {}).", filmId, directors);
        Collection<Long> directorId = directors.stream().map(Director::getId).toList();
        directorService.findNotValid(directorId);
        filmDirectorDao.deleteDirectorByFilm(filmId);
        filmDirectorDao.addSet(filmId,directors);
        log.trace("Фильму ID_{} обновлены режиссеры {}.", filmId, directors);

        return directorService.getDirectorByListId(directorId.stream().toList()).values();
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
        List<Long> idPopularFilms = popFilm.stream()
                .map(Film::getId)
                .toList();
        Collection<Film> allFilm = filmStorage.findAll();
        allFilm.forEach(film -> {
            if (!idPopularFilms.contains(film.getId())) {
                popFilm.add(film);
            }
        });
        return getFieldsFilm(popFilm);

    }

    public void deleteFilm(Long filmId) {
        log.info("Start delete film with ID = {}", filmId);
        findFilmById(filmId);
        filmStorage.delByFilmId(filmId);
    }

    public List<Film> getDirectorFilms(long directorId,String sortBy) {
        SortBy sort = SortBy.from(sortBy);
        if (sort == null) {
            throw new ParameterNotValidException("Получено: " + sortBy + " должно быть: likes или year");
        }

        List<Long> filmIdList = filmStorage.getFilmDirectorSort(directorId,sort);
        Collection<Film> fl = getFieldsFilm(filmStorage.getFilmsByListFilmId(filmIdList));
        Map<Long,Film> mapFilm = new HashMap<>();
        for (Film el:fl) {
            mapFilm.put(el.getId(),el);
        }
        List<Film> result = new ArrayList<>();
        for (Long i: filmIdList) {
            result.add(mapFilm.get(i));
        }
        return result;
    }

    public Collection<Film> searchFilmOrDirector(String query, String by) {
        List<Film> searchFilms;
        if (query.equals("Empty") || by.equals("Not argument")) {
            searchFilms = new ArrayList<>(getPopularFilms(1000));
        } else {
            String[] allArgs = by.split(",");
            if (allArgs.length == 2) {
                searchFilms = filmStorage.searchFilmByTitleAndDirector(query);
            } else {
                if (allArgs[0].equals("director")) {
                    searchFilms = filmStorage.searchFilmByDirector(query);
                } else {
                    searchFilms = filmStorage.searchFilmByTitle(query);
                }
            }
        }
        return getFieldsFilm(searchFilms);
    }
}

package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {
    private final FilmService filmService;
    private final UserService userService;
    private final ReviewDbStorage reviewDbStorage;

   private final User user1 = User.builder().email("test-one@mail.ru").login("login-A")
           .name("name-1").birthday(LocalDate.of(1980,3,5)).build();

   private final Film film1 = Film.builder().name("name фильма А").description("описание фильма А")
           .releaseDate(LocalDate.of(1970,1,1)).duration(100)
           .genres(Set.of(new Genre(1L,""))).mpa(new Mpa(1L,"")).build();


   @Test
    void create() {
       film1.setDirectors(List.of());
       filmService.create(film1);
       userService.create(user1);
       Review filmReview = new Review();
       filmReview.setFilmId(film1.getId());
       filmReview.setUserId(user1.getId());
       filmReview.setContent("Test");
       filmReview.setIsPositive(false);
       filmReview.setUseful(2);
       Review checkReview = reviewDbStorage.create(filmReview);
       filmReview.setReviewId(checkReview.getReviewId());
       Assertions.assertEquals(filmReview, checkReview);
   }

    @Test
    void findAll() {
        reviewDbStorage.deleteAll();
        film1.setDirectors(List.of());
        filmService.create(film1);
        userService.create(user1);
        Review filmReview = new Review();
        filmReview.setFilmId(film1.getId());
        filmReview.setUserId(user1.getId());
        filmReview.setContent("Test");
        filmReview.setIsPositive(false);


        Review filmReview2 = new Review();
        filmReview2.setFilmId(film1.getId());
        filmReview2.setUserId(user1.getId());
        filmReview2.setContent("Test2");
        filmReview2.setIsPositive(true);


        Review createReview1 = reviewDbStorage.create(filmReview);
        Review createReview2 = reviewDbStorage.create(filmReview2);
        Assertions.assertEquals(List.of(createReview1, createReview2), reviewDbStorage.findAll());
    }

   @Test
    void update() {
       film1.setDirectors(List.of());
       filmService.create(film1);
       userService.create(user1);
       Review filmReview = new Review();
       filmReview.setFilmId(film1.getId());
       filmReview.setUserId(user1.getId());
       filmReview.setContent("Test");
       filmReview.setIsPositive(false);
       filmReview.setUseful(2);
       Review checkReview = reviewDbStorage.create(filmReview);
       checkReview.setContent("UpdateTest");
       Review updateReview = reviewDbStorage.update(checkReview);
       Assertions.assertEquals(checkReview, updateReview);
   }

   @Test
    void findReviewById() {
       film1.setDirectors(List.of());
       filmService.create(film1);
       userService.create(user1);
       Review filmReview = new Review();
       filmReview.setFilmId(film1.getId());
       filmReview.setUserId(user1.getId());
       filmReview.setContent("Test");
       filmReview.setIsPositive(false);
       Review createReview = reviewDbStorage.create(filmReview);
       filmReview.setReviewId(createReview.getReviewId());
       reviewDbStorage.delByReviewId(createReview.getReviewId());
       Assertions.assertFalse(reviewDbStorage.findAll().contains(createReview));
   }

   @Test
    void delByReviewId() {
       film1.setDirectors(List.of());
       filmService.create(film1);
       userService.create(user1);
       Review filmReview = new Review();
       filmReview.setFilmId(film1.getId());
       filmReview.setUserId(user1.getId());
       filmReview.setContent("Test");
       filmReview.setIsPositive(false);
       Review createReview = reviewDbStorage.create(filmReview);
       reviewDbStorage.delByReviewId(createReview.getReviewId());
       Assertions.assertEquals(Optional.empty(), reviewDbStorage.findReviewById(createReview.getReviewId()));
   }

   @Test
    void findReviewsByFilmId() {
       film1.setDirectors(List.of());
       filmService.create(film1);
       userService.create(user1);
       Review filmReview = new Review();
       filmReview.setFilmId(film1.getId());
       filmReview.setUserId(user1.getId());
       filmReview.setContent("Test");
       filmReview.setIsPositive(false);


       Review filmReview2 = new Review();
       filmReview2.setFilmId(film1.getId());
       filmReview2.setUserId(user1.getId());
       filmReview2.setContent("Test2");
       filmReview2.setIsPositive(true);


       Review createReview1 = reviewDbStorage.create(filmReview);
       Review createReview2 = reviewDbStorage.create(filmReview2);
       Assertions.assertEquals(List.of(createReview1, createReview2),
               reviewDbStorage.findReviewsByFilmId(film1.getId()));
   }

}

# java-filmorate
Данный проект - это сервис для кинотеки. На сервисе можно добавлять друзей и получать рекомендации на основе их лайков.

### Схема БД
<img alt="Database diagram" src="./src/main/resources/Schema_BD_filmorate.jpg">

### Примеры запросов

<details>
    <summary><h3>Для фильмов:</h3></summary>

* `Создание` фильма:

```SQL
INSERT INTO films (name,
                   description,
                   releaseDate,
                   duration,
                   mpa_id)
VALUES (?, ?, ?, ?, ?);
```

* `Обновление` фильма:

```SQL
UPDATE
    films
SET name                = ?,
    description         = ?,
    releaseDate        = ?,
    duration           = ?,
    mpa_id             = ?
WHERE id = ?;
```

* `Получение` фильма `по идентификатору`:

```SQL
SELECT f.id,
       f.name,
       f.description,
       f.releaseDate,
       f.duration,
       mp.name AS mpa_rating,
       g.name  AS genre
FROM films f
         JOIN mpas mp ON f.mpa_id = mp.id
         JOIN film_genres fg ON f.id = fg.film_id
         JOIN genres g ON fg.genre_id = g.id
WHERE f.id = ?;
```   

* `Получение всех` фильмов:

```SQL
SELECT f.id,
       f.name,
       f.description,
       f.releaseDate,
       f.duration,
       mp.name AS mpa_rating,
       STRING_AGG(g.name, ', ' ORDER BY g.name) AS genres
FROM films f
         JOIN mpas mp ON f.mpa_id = mp.id
         JOIN film_genres fg ON f.id = fg.film_id
         JOIN genres g ON fg.genre_id = g.id
GROUP BY f.id;
```

* `Получение топ-N (по количеству лайков)` фильмов:
```SQL
SELECT f.id,
       f.name,
       f.description,
       f.releaseDate,
       f.duration,
       mp.name AS mpa_rating,
       g.name AS genre,
       COUNT(fl.user_id) AS like_count
FROM films f
         JOIN mpas mp ON f.mpa_id = mp.id
         JOIN film_genres fg ON f.id = fg.film_id
         JOIN genres g ON fg.genre_id = g.id
         LEFT JOIN likes fl ON f.id = fl.film_id
GROUP BY f.film_id,
         mp.name,
         g.name
ORDER BY like_count DESC LIMIT ?;
```
</details>

<details>
    <summary><h3>Для пользователей:</h3></summary>

* `Создание` пользователя:

```SQL
INSERT INTO users (email,
                   login,
                   name,
                   birthday)
VALUES (?, ?, ?, ?)
```

* `Обновление` пользователя:

```SQL
UPDATE
    users
SET email    = ?,
    login    = ?,
    name     = ?,
    birthday = ?
WHERE id = ?
```

* `Получение` пользователя `по идентификатору`:

```SQL
SELECT *
FROM users
WHERE id = ?
```   

* `Получение всех` пользователей:

```SQL
SELECT *
FROM users
``` 

* `Получение списка друзей` пользователя:

```SQL
SELECT *
FROM users u
WHERE u.id in (SELECT f.friend_id FROM friends as f WHERE f.user_id =2)
```

* `Получение общего списка друзей` между пользователем (id) и его другом(friend_id):

```SQL
SELECT *
FROM users
WHERE id IN (SELECT str1.friends_id
			FROM (select f.friend_id FROM friends as f WHERE f.user_id =2) str1
			JOIN (select f.friend_id FROM friends as f WHERE f.user_id =1) str2 on str1.friend_id = str2.friend_id)
``` 


</details>

<details>
    <summary><h3>Для жанров:</h3></summary>

* `Получение` жанра `по идентификатору`:

```SQL
SELECT *
FROM genres
WHERE genre_id = ?
``` 

* `Получение всех` жанров:

```SQL
SELECT *
FROM genres
```   
</details>

<details>
    <summary><h3>Для рейтингов MPA:</h3></summary>

* `Получение` рейтинга MPA `по идентификатору`:

```SQL
SELECT *
FROM mpas
WHERE mpa_id = ?
``` 

* `Получение всех` рейтингов MPA:

```SQL
SELECT *
FROM mpas
```   
</details>

## Валидация

Входные данные, поступающие в запросе,
должны соответствовать определенным критериям:

<details>
    <summary><h3>Для фильмов:</h3></summary>

* Название фильма должно быть указано и не может быть пустым
* Максимальная длина описания фильма не должна превышать 200 символов
* Дата релиза фильма должна быть не раньше 28 декабря 1895 года[^1]
* Продолжительность фильма должна быть положительной
* Рейтинг фильма должен быть указан

</details>

<details>
    <summary><h3>Для пользователей:</h3></summary>

* Электронная почта пользователя должна быть указана и соответствовать формату email
* Логин пользователя должен быть указан и не содержать пробелов
* Дата рождения пользователя должна быть указана и не может быть в будущем

</details>

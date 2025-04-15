--DROP TABLE mpas CASCADE;
--DROP TABLE films CASCADE;
--DROP TABLE genres CASCADE;
--DROP TABLE film_genres CASCADE;
--DROP TABLE users CASCADE;
--DROP TABLE friends CASCADE;
--DROP TABLE likes CASCADE;
--DROP TABLE film_mpas CASCADE;
--DROP TABLE directors CASCADE;
--DROP TABLE film_directors CASCADE;

create table if not exists genres
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name varchar(100) not null
);

create table if not exists mpas
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name varchar(100) not null
);

create table if not exists users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
	email    varchar(255) not null,
	login    varchar(255) not null,
    name     varchar(255),    
    birthday date         not null    
);

create table if not exists friends
(
    user_id   BIGINT,
    friend_id BIGINT,
	confirmed boolean,
    foreign key (user_id) references users (id) on delete cascade,
    foreign key (friend_id) references users (id) on delete cascade,
    primary key (user_id, friend_id)
);

create table if not exists films
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         varchar(255) not null,
	description  varchar(200),
    release_date date,    
    duration     int
);

create table if not exists likes
(
    film_id BIGINT,
    user_id BIGINT,
    foreign key (film_id) references films (id) on delete cascade,
    foreign key (user_id) references users (id) on delete cascade,
    primary key (film_id, user_id)
);

create table if not exists film_genres
(
    film_id  BIGINT,
    genre_id BIGINT,
    foreign key (film_id) references films (id) on delete cascade,
    foreign key (genre_id) references genres (id) on delete cascade,
    primary key (film_id, genre_id)
);

create table if not exists film_mpas
(
    film_id BIGINT,
	mpa_id  BIGINT,
	foreign key (film_id) references films (id) on delete cascade,
    foreign key (mpa_id) references mpas (id) on delete cascade,    
    primary key (film_id, mpa_id)
);

create table if not exists directors
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        varchar(255) not null
);

create table if not exists film_directors
(
    film_id     BIGINT,
    director_id BIGINT,
    foreign key (film_id) references films (id) on delete cascade,
    foreign key (director_id) references directors (id) on delete cascade,
    primary key (film_id,director_id)
 );

create table if not exists feed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    event_type VARCHAR(100) NOT NULL,
    operation VARCHAR(50) NOT NULL,
    event_id BIGINT,
    timestamp TIMESTAMP,
    foreign key (user_id) references users (id) on delete cascade
);




 create table if not exists reviews
 (
     id             BIGINT AUTO_INCREMENT PRIMARY KEY,
     content        varchar,
     is_positive    boolean,
     film_id        BIGINT,
     user_id        BIGINT,
     useful         int,
     foreign key (film_id) references films (id) on delete cascade,
     foreign key (user_id) references users (id) on delete cascade
 );

 create table if not exists review_users
 (
     review_id      BIGINT,
     user_id        BIGINT,
     is_useful      boolean not null,
     primary key (review_id,user_id),
     foreign key (user_id) references users (id) on delete cascade,
     foreign key (review_id) references reviews (id) on delete cascade
     );
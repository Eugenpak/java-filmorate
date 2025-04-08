
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
    foreign key (user_id) references users (id),
    foreign key (friend_id) references users (id),
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
    director_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        varchar(255) not null
);

create table if not exists film_directors
(
    director_id BIGINT,
    film_id     BIGINT,
    primary key (director_id, film_id),
    foreign key (director_id) references directors (director_id) on delete cascade,
    foreign key (film_id) references films (id) on delete cascade
);
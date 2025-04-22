package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    Long reviewId;
    @NotNull String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Integer useful;
}

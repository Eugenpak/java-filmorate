package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;// - целочисленный идентификатор — id;
    @NotBlank(message = "Имя фильма обязательно")
    private String name;// - название —  name;
    @Size(min = 0, max = 200, message = "Описание фильма должно содержать до 200 символов")
    private String description;// - описание — description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;// - дата релиза — releaseDate;
    @Min(value = 0, message = "Продолжительность фильма >= 0")
    private Integer duration;// - продолжительность фильма — duration.
    private final Set<Long> likes  = new HashSet<>();
}

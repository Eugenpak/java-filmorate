package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

@Data
@Builder
@Slf4j
public class User {
    private Long id; //— уникальный идентификатор пользователя
    @NotBlank
    @Email(message = "Недопустимый email. Попробуйте снова.")
    private String email; //— электронная почта — email;
    @NotBlank
    private String login; //— логин пользователя — login;
    private String name; //— имя для отображения — name;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthday; //— дата рождения — birthday.
}



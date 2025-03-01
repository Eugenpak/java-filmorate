package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id; //— уникальный идентификатор пользователя
    @NotBlank(message = "must not be blank")
    @Email(message = "Недопустимый email. Попробуйте снова.")
    private String email; //— электронная почта — email;
    @NotBlank(message = "must not be blank")
    private String login; //— логин пользователя — login;
    private String name; //— имя для отображения — name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday; //— дата рождения — birthday.
    private final Set<Long> friends  = new HashSet<>();
}



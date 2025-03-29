package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id; //— уникальный идентификатор пользователя
    @NotBlank(message = "must not be blank")
    @Email(message = "Недопустимый email. Попробуйте снова.")
    private String email; //— электронная почта — email;
    @NotBlank(message = "must not be blank")
    private String login; //— логин пользователя — login;
    private String name; //— имя для отображения — name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday; //— дата рождения — birthday.
    //private final Set<Long> friends  = new HashSet<>();
}



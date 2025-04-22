package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserFeed {
    private Long eventId;
    @NotBlank
    private Long userId;
    @NotBlank
    private String eventType;
    @NotBlank
    private String operation;
    @NotBlank
    private Long entityId;
    private Long timestamp;
}

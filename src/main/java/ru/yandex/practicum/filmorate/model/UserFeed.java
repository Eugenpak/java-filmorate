package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserFeed {
    private Long id;
    @NotBlank
    private Long userId;
    @NotBlank
    private String eventType;
    @NotBlank
    private String operation;
    @NotBlank
    private Long eventId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeStamp;
}

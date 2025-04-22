package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUser {
    private Long reviewId;
    private Long userId;
    private Boolean isUseful;

    public int getUsefulValue() {
        if (isUseful) {
            return 1;
        } else {
            return -1;
        }
    }
}

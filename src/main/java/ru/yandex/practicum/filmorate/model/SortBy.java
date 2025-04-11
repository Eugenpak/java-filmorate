package ru.yandex.practicum.filmorate.model;

public enum SortBy {
    LIKES, YEAR;

    // Преобразует строку в элемент перечисления
    public static SortBy from(String order) {
        return switch (order.toLowerCase()) {
            case "likes", "l" -> LIKES;
            case "year", "y" -> YEAR;
            default -> null;
        };
    }
}

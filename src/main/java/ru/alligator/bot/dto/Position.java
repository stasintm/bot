package ru.alligator.bot.dto;

/**
 * Должность.
 *
 */
public enum Position {

    /**
     * Генеральный директор.
     */
    CEO("Генеральный директор"),

    /**
     * Руководитель отдела.
     */
    HEAD_OF_DEPARTMENT("Руководитель отдела"),

    /**
     * Разработчик.
     */
    DEVELOPER("Разработчик"),

    /**
     * Менеджер проектов.
     */
    PROJECT_MANAGER("Менеджер проектов"),

    /**
     * Бухгалтер.
     */
    ACCOUNTANT("Бухгалтер"),

    /**
     * Юрист.
     */
    LAWYER("Юрист"),

    /**
     * HR-менеджер.
     */
    HR_MANAGER("HR-менеджер");

    private final String russianName;

    public String getRussianName() {
        return russianName;
    }

    Position(String russianName) {
        this.russianName = russianName;
    }
}
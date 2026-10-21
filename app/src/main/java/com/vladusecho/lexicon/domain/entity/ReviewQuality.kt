package com.vladusecho.lexicon.domain.entity

enum class ReviewQuality(
    val grade: Int,
    val label: String
) {
    AGAIN(grade = 1, label = "Снова"),
    HARD(grade = 3, label = "Трудно"),
    GOOD(grade = 4, label = "Хорошо"),
    EASY(grade = 5, label = "Легко")
}

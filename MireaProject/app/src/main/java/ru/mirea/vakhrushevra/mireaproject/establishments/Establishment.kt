package ru.mirea.vakhrushevra.mireaproject.establishments

data class Establishment(
    val id: Int,
    val name: String,
    val address: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

object EstablishmentsData {
    val establishments = listOf(
        Establishment(
            id = 1,
            name = "РТУ МИРЭА",
            address = "Проспект Вернадского, 78",
            description = "Технологический университет, один из крупнейших вузов России в области IT и радиоэлектроники.",
            latitude = 55.703317,
            longitude = 37.530699
        ),
        Establishment(
            id = 2,
            name = "Большой театр",
            address = "Театральная площадь, 1",
            description = "Главная оперная и балетная сцена России, исторический символ московской культуры.",
            latitude = 55.760094,
            longitude = 37.618423
        ),
        Establishment(
            id = 3,
            name = "Парк Горького",
            address = "Крымский Вал, 9",
            description = "Центральный парк культуры и отдыха на берегу Москвы-реки с музеями, аллеями и набережной.",
            latitude = 55.731012,
            longitude = 37.601379
        ),
        Establishment(
            id = 4,
            name = "ВДНХ",
            address = "Проспект Мира, 119",
            description = "Выставочный комплекс с павильонами, фонтанами и крупными культурными мероприятиями.",
            latitude = 55.831312,
            longitude = 37.631469
        )
    )
}

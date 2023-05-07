package com.vereshchagin.nikolay.pepegafood.ui.home.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Объект авто-корзины для главной страницы.
 */
@Entity(tableName = "shopping_basket")
class ShoppingBasket(
    @PrimaryKey
    val id: Int,
    val text: String
)
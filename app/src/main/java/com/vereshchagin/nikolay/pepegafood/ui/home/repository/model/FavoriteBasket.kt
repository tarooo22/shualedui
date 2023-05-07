package com.vereshchagin.nikolay.pepegafood.ui.home.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Объект избранной корзины для главной странице.
 */
@Entity(tableName = "favorite_basket")
class FavoriteBasket(
    @PrimaryKey
    val id: Int,
    val title: String,
    val cost: String
)
package com.vereshchagin.nikolay.pepegafood.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "basket_item")
data class BasketItem (
    @PrimaryKey val id: Int,
    val title: String,
    val preview: String,
    val cost: String,
    val orderIndex: Int
)
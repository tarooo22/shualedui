package com.vereshchagin.nikolay.pepegafood.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_item")
data class CatalogItem (
    @PrimaryKey val id: Int,
    val title: String,
    val preview: String,
    val orderIndex: Int = -1
)
package com.vereshchagin.nikolay.pepegafood.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.vereshchagin.nikolay.pepegafood.model.BasketItem

@Dao
interface BasketDao {

    @Query("SELECT * FROM basket_item ORDER BY orderIndex ASC")
    fun all() : DataSource.Factory<Int, BasketItem>
}
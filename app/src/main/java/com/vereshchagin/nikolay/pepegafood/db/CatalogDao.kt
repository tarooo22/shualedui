package com.vereshchagin.nikolay.pepegafood.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vereshchagin.nikolay.pepegafood.model.CatalogItem

@Dao
interface CatalogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<CatalogItem>)

    @Query("SELECT * FROM catalog_item ORDER BY orderIndex ASC")
    fun all() : DataSource.Factory<Int, CatalogItem>

    @Query("DELETE FROM catalog_item")
    fun clear()
}
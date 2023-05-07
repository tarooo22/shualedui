package com.vereshchagin.nikolay.pepegafood.ui.home.repository.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.CatalogHomeItem
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.FavoriteBasket
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.ShoppingBasket


@Dao
interface HomeDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppingBaskets(baskets: List<ShoppingBasket>)


    @Query("SELECT * FROM shopping_basket ORDER BY id ASC")
    fun shoppingBaskets() : DataSource.Factory<Int, ShoppingBasket>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteBaskets(baskets: List<FavoriteBasket>)


    @Query("SELECT * FROM favorite_basket ORDER BY id ASC")
    fun favoriteBaskets() : DataSource.Factory<Int, FavoriteBasket>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCatalogItems(baskets: List<CatalogHomeItem>)


    @Query("SELECT * FROM catalog_home_item ORDER BY id ASC")
    fun catalogItems() : DataSource.Factory<Int, CatalogHomeItem>
}
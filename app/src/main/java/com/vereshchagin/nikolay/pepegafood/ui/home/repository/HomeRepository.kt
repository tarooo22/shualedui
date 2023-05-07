package com.vereshchagin.nikolay.pepegafood.ui.home.repository

import android.content.Context
import androidx.paging.LivePagedListBuilder
import com.vereshchagin.nikolay.pepegafood.MainApplicationDatabase
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.CatalogHomeItem
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.FavoriteBasket
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.ShoppingBasket
import java.util.concurrent.Executors

/**
 * Репозиторий для загрузки информации для главной страницы.
 */
class HomeRepository(context: Context) {

    private val db = MainApplicationDatabase.database(context)
    private val dao = db.home()

    private val ioExecutor = Executors.newSingleThreadExecutor()

    /**
     * Возвращает Listing для отображения на главной страницы.
     */
    fun content() : HomeListing {
        ioExecutor.execute {
            db.runInTransaction {
                dao.insertShoppingBaskets(
                    listOf(
                        ShoppingBasket(1, "500 р"),
                        ShoppingBasket(2, "1000 р"),
                        ShoppingBasket(3, "2500 р"),
                        ShoppingBasket(4, "5000 р")
                    )
                )
                dao.insertFavoriteBaskets(
                    listOf(
                        FavoriteBasket(1, "Дошираки", "25565 р")
                    )
                )
                val list = ArrayList<CatalogHomeItem>()
                for (index in 0..50) {
                    list.add(CatalogHomeItem(index, "Category: $index"))
                }
                dao.insertCatalogItems(list)
            }
        }

        val shoppingBaskets = LivePagedListBuilder(dao.shoppingBaskets(), 10)
            .build()

        val favoriteBaskets = LivePagedListBuilder(dao.favoriteBaskets(), 10)
            .build()

        val catalogItems = LivePagedListBuilder(dao.catalogItems(), 10)
            .build()

        return HomeListing(
            shoppingBaskets,
            favoriteBaskets,
            catalogItems
        )
    }
}
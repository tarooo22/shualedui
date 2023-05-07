package com.vereshchagin.nikolay.pepegafood.ui.home.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.CatalogHomeItem
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.FavoriteBasket
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.ShoppingBasket

/**
 * Listing с объектами, которые будут отображаться на главной странице.
 */
class HomeListing (
    val shoppingBaskets: LiveData<PagedList<ShoppingBasket>>,
    val favoriteBaskets: LiveData<PagedList<FavoriteBasket>>,
    val catalogItems: LiveData<PagedList<CatalogHomeItem>>
)
package com.vereshchagin.nikolay.pepegafood.repository.boundary

import com.vereshchagin.nikolay.pepegafood.api.PepegaFoodApi
import com.vereshchagin.nikolay.pepegafood.api.responses.CatalogItemResponse
import com.vereshchagin.nikolay.pepegafood.db.CatalogDao
import com.vereshchagin.nikolay.pepegafood.model.CatalogItem
import java.util.concurrent.Executor


class CatalogBoundaryCallback(
    api: PepegaFoodApi,
    dao: CatalogDao,
    ioExecutor: Executor,
    handleResponse: (CatalogItemResponse?) -> Unit
) : BaseBoundaryCalback<PepegaFoodApi, CatalogDao, CatalogItemResponse, CatalogItem>(
    api,
    dao,
    ioExecutor,
    handleResponse
) {

    override fun onZeroLoaded() {

    }

    override fun onEndLoaded(itemAtEnd: CatalogItem) {

    }
}
package com.vereshchagin.nikolay.pepegafood.repository.boundary

import com.vereshchagin.nikolay.pepegafood.api.PepegaFoodApi
import com.vereshchagin.nikolay.pepegafood.api.responses.BasketItemResponse
import com.vereshchagin.nikolay.pepegafood.db.BasketDao
import com.vereshchagin.nikolay.pepegafood.model.BasketItem
import java.util.concurrent.Executor

class BasketBoundaryCallback(
    api: PepegaFoodApi,
    dao: BasketDao,
    ioExecutor: Executor,
    handleResponse: (BasketItemResponse?) -> Unit
) : BaseBoundaryCalback<PepegaFoodApi, BasketDao, BasketItemResponse, BasketItem>(
    api,
    dao,
    ioExecutor,
    handleResponse
)  {
    override fun onZeroLoaded() {

    }

    override fun onEndLoaded(itemAtEnd: BasketItem) {

    }
}
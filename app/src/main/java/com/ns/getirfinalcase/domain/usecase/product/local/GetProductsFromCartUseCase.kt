package com.ns.getirfinalcase.domain.usecase.product.local

import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.repository.local.LocalProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsFromCartUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) {

    operator fun invoke(): Flow<BaseResponse<List<Product>>>? =
        localProductRepository.getProductsFromCart()
}
package com.ns.getirfinalcase.domain.usecase.product.remote

import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.domain.model.product.ProductResponse
import com.ns.getirfinalcase.domain.repository.remote.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    operator fun invoke(): Flow<BaseResponse<List<ProductResponse>>> =
        productRepository.getAllProducts()
}
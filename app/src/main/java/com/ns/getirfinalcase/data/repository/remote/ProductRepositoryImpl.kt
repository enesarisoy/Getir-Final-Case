package com.ns.getirfinalcase.data.repository.remote

import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.data.source.remote.service.ProductService
import com.ns.getirfinalcase.di.IoDispatcher
import com.ns.getirfinalcase.domain.model.product.ProductResponse
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProductResponse
import com.ns.getirfinalcase.domain.repository.remote.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productService: ProductService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ProductRepository {

    override fun getAllProducts(): Flow<BaseResponse<List<ProductResponse>>> {
        TODO("Not yet implemented")
    }

    override fun getSuggestedProducts(): Flow<BaseResponse<List<SuggestedProductResponse>>> {
        TODO("Not yet implemented")
    }
}
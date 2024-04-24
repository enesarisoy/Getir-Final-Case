package com.ns.getirfinalcase.domain.repository.remote

import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.domain.model.product.ProductResponse
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProductResponse
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getAllProducts(): Flow<BaseResponse<List<ProductResponse>>>

    fun getSuggestedProducts(): Flow<BaseResponse<List<SuggestedProductResponse>>>
}
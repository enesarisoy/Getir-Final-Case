package com.ns.getirfinalcase.domain.repository.local

import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

interface LocalProductRepository {

    suspend fun addToCart(product: Product)
    fun getProductsFromCart(): Flow<BaseResponse<List<Product>>>?
    fun getProductById(productId: String): Flow<Product?>
    suspend fun deleteFromCart(id: String)
    suspend fun deleteAllItemsInCart()

}
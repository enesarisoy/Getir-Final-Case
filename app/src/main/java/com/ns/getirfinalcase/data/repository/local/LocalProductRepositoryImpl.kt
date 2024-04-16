package com.ns.getirfinalcase.data.repository.local

import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.data.source.local.ProductsDao
import com.ns.getirfinalcase.di.IoDispatcher
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.repository.local.LocalProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalProductRepositoryImpl @Inject constructor(
    private val productsDao: ProductsDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalProductRepository {

    override suspend fun addToCart(product: Product) = productsDao.addToCart(product)

    override fun getProductsFromCart(): Flow<BaseResponse<List<Product>>>? {
        return productsDao.getProductsFromCart()?.map { productList ->
            BaseResponse.Success(productList)
        }?.catch { e ->
            BaseResponse.Error<List<Product>>(e.message ?: "Error")
        }?.flowOn(ioDispatcher)
    }


    override fun getProductById(productId: String): Flow<Product?> {
        return productsDao.getProductById(productId)
    }

    override suspend fun deleteFromCart(id: String) {
        return productsDao.deleteFromCart(id)
    }

    override suspend fun deleteAllItemsInCart() {
        return productsDao.deleteAllItemsInCart()
    }

    override fun getTotalPriceInCart(): Flow<Double?> {
        return productsDao.getTotalPriceInCart()
    }
}
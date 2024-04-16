package com.ns.getirfinalcase.domain.usecase.product.local

import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.repository.local.LocalProductRepository
import javax.inject.Inject

class AddToCartProductUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
){

    suspend operator fun invoke(product: Product) = localProductRepository.addToCart(product)
}
package com.ns.getirfinalcase.domain.usecase.product.local

import com.ns.getirfinalcase.domain.repository.local.LocalProductRepository
import javax.inject.Inject

class UpdateProductsUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
){
    suspend operator fun invoke(productId: String, newQuantity: Int) {
        localProductRepository.updateQuantity(productId, newQuantity)
    }
}
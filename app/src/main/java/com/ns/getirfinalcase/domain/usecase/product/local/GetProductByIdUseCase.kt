package com.ns.getirfinalcase.domain.usecase.product.local

import com.ns.getirfinalcase.domain.repository.local.LocalProductRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) {

    suspend operator fun invoke(productId: String) =
        localProductRepository.getProductById(productId)
}
package com.ns.getirfinalcase.domain.usecase.product.local

import com.ns.getirfinalcase.domain.repository.local.LocalProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalPriceInChartUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) {

    operator fun invoke(): Flow<Double?> =
        localProductRepository.getTotalPriceInCart()
}
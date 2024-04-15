package com.ns.getirfinalcase.domain.model.product

data class ProductResponse(
    val id: String,
    val name: String,
    val productCount: Int,
    val products: List<Product>
)
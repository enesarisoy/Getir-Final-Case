package com.ns.getirfinalcase.domain.model.suggested_product

data class SuggestedProductResponse(
    val id: String,
    val name: String,
    val products: List<SuggestedProduct>
)
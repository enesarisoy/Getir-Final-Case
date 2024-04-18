package com.ns.getirfinalcase.domain.model.suggested_product

data class SuggestedProduct(
    val category: String?,
    val id: String,
    val imageURL: String?,
    val name: String?,
    val price: Double?,
    val priceText: String?,
    val shortDescription: String?,
    val squareThumbnailURL: String?,
    val status: Int?,
    val unitPrice: Double?,
    val quantity: Int? = 1
)
package com.ns.getirfinalcase.domain.model.product

data class Product(
    val attribute: String? = null,
    val id: String,
    val imageURL: String? = null,
    val name: String? = null,
    val price: Double,
    val priceText: String? = null,
    val shortDescription: String? = null,
    val thumbnailURL: String? = null,
    var quantity: Int = 1
)
package com.ns.getirfinalcase.data.mapper

import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProduct

fun SuggestedProduct.toProduct(): Product {
    return Product(
        id = id,
        name = name ?: "",
        imageURL = imageURL ?: squareThumbnailURL,
        price = price ?: 0.0,
        priceText = priceText ?: "",
        shortDescription = shortDescription ?: "",
        thumbnailURL = squareThumbnailURL ?: imageURL,
        quantity = quantity ?: 0
    )
}
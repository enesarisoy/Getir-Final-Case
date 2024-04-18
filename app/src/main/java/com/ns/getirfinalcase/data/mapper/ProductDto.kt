package com.ns.getirfinalcase.data.mapper

import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProduct

fun Product.toSuggestedProduct(): SuggestedProduct {
    return SuggestedProduct(
        id = id,
        name = name ?: "",
        imageURL = imageURL ?: "",
        price = price,
        priceText = priceText ?: "",
        shortDescription = shortDescription ?: "",
        squareThumbnailURL = thumbnailURL ?: "",
        quantity = quantity ?: 0,
        category = null,
        status = null,
        unitPrice = price
    )
}
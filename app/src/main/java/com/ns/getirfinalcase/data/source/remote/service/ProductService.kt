package com.ns.getirfinalcase.data.source.remote.service

import com.ns.getirfinalcase.domain.model.product.ProductResponse
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProductResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProductService {

    @GET("products")
    fun getAllProducts(): Call<List<ProductResponse>>

    @GET("suggestedProducts")
    fun getSuggestedProducts(): Call<List<SuggestedProductResponse>>
}
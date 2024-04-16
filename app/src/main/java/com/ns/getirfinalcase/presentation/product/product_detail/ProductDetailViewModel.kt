package com.ns.getirfinalcase.presentation.product.product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.usecase.product.local.AddToCartProductUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.DeleteFromCartUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetProductByIdUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetProductsFromCartUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetTotalPriceInChartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getTotalPriceInChartUseCase: GetTotalPriceInChartUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartProductUseCase: AddToCartProductUseCase,
    private val deleteFromCartUseCase: DeleteFromCartUseCase
) : ViewModel() {

    private var _getTotalPrice: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val getTotalPrice = _getTotalPrice.asStateFlow()

    private var _getProductById: MutableStateFlow<Product?> = MutableStateFlow(null)
    val getProductById = _getProductById.asStateFlow()

    private var _addToCart: MutableStateFlow<Product?> = MutableStateFlow(null)
    val addToCart = _addToCart.asStateFlow()

    init {
        getTotalPrice()
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val checkProductAlreadyInDatabase = getProductByIdUseCase(product.id).firstOrNull()

            checkProductAlreadyInDatabase?.let {
                it.quantity++
                addToCartProductUseCase(it)
                _addToCart.value = it
            } ?: run {
                addToCartProductUseCase(product)
                _addToCart.value = product
            }
            getTotalPrice()

        }
    }

    fun deleteFromCart(product: Product) {
        viewModelScope.launch {
            val checkProductAlreadyInDatabase = getProductByIdUseCase(product.id).firstOrNull()

            checkProductAlreadyInDatabase?.let {
                if (it.quantity > 1) {
                    it.quantity--
                    // Updates the current product quantity with minus 1.
                    // Used this because room's @Update not working correctly. (Or I couldn't manage it.)
                    addToCartProductUseCase(it)
                } else {
                    deleteFromCartUseCase(product.id)
                }
            }
            getTotalPrice()
        }
    }

    fun getProductById(product: Product) {
        viewModelScope.launch {
            val productInCart = getProductByIdUseCase(product.id).firstOrNull()
            productInCart?.let {
                _getProductById.value = it
            }
        }
    }


    fun getTotalPrice() {
        viewModelScope.launch {
            val price = getTotalPriceInChartUseCase.invoke().firstOrNull()
            price?.let { totalPrice ->
                _getTotalPrice.value = totalPrice
            }
        }
    }
}
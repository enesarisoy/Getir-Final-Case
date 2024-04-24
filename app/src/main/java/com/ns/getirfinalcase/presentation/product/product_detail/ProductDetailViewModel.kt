package com.ns.getirfinalcase.presentation.product.product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.core.util.Constants.SHORT_FAKE_DELAY
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.usecase.product.local.AddToCartProductUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.DeleteFromCartUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetProductByIdUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetTotalPriceInChartUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.UpdateProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val deleteFromCartUseCase: DeleteFromCartUseCase,
    private val updateProductsUseCase: UpdateProductsUseCase
) : ViewModel() {

    private var _getTotalPrice: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val getTotalPrice = _getTotalPrice.asStateFlow()

    private var _getProductById: MutableStateFlow<ViewState<BaseResponse<Product?>>> =
        MutableStateFlow(ViewState.Loading)
    val getProductById = _getProductById.asStateFlow()

    private var _addToCart: MutableStateFlow<ViewState<BaseResponse<Product?>>> =
        MutableStateFlow(ViewState.Loading)
    val addToCart = _addToCart.asStateFlow()

    fun addToCart(product: Product) {
        viewModelScope.launch {
            _addToCart.value = ViewState.Loading

            delay(SHORT_FAKE_DELAY)

            val checkProductAlreadyInDatabase = getProductByIdUseCase(product.id).firstOrNull()

            checkProductAlreadyInDatabase?.let { existingProduct ->
                existingProduct.quantity++
                updateProductsUseCase.invoke(existingProduct.id, existingProduct.quantity)
                _addToCart.value = ViewState.Success(BaseResponse.Success(existingProduct))
            } ?: run {
                product.quantity = 1
                addToCartProductUseCase(product)
                _addToCart.value = ViewState.Success(BaseResponse.Success(product))
            }

            getTotalPrice()
        }
    }

    fun deleteFromCart(product: Product) {
        viewModelScope.launch {
            _addToCart.value = ViewState.Loading

            delay(SHORT_FAKE_DELAY)

            val checkProductAlreadyInDatabase = getProductByIdUseCase(product.id).firstOrNull()

            checkProductAlreadyInDatabase?.let { existingProduct ->
                if (existingProduct.quantity > 1) {
                    existingProduct.quantity--
                    updateProductsUseCase.invoke(existingProduct.id, existingProduct.quantity)
                    _addToCart.value = ViewState.Success(BaseResponse.Success(existingProduct))
                } else {
                    deleteFromCartUseCase(product.id)
                    _addToCart.value = ViewState.Success(BaseResponse.Success(null))
                }
            } ?: run {
                _addToCart.value = ViewState.Error("Product not found")
            }

            getTotalPrice()
        }
    }

    fun getProductById(product: Product) {
        viewModelScope.launch {
            _getProductById.value = ViewState.Loading

            delay(SHORT_FAKE_DELAY)

            val productInCart = getProductByIdUseCase(product.id).firstOrNull()

            productInCart?.let {
                _getProductById.value = ViewState.Success(BaseResponse.Success(it))
            } ?: run {
                _getProductById.value = ViewState.Success(BaseResponse.Success(null))
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
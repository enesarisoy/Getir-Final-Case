package com.ns.getirfinalcase.presentation.shopping_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.model.product.ProductResponse
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProductResponse
import com.ns.getirfinalcase.domain.usecase.product.local.DeleteAllItemsInCartUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetProductsFromCartUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetTotalPriceInChartUseCase
import com.ns.getirfinalcase.domain.usecase.product.remote.GetSuggestedProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingCartViewModel @Inject constructor(
    private val getProductsFromCartUseCase: GetProductsFromCartUseCase,
    private val getSuggestedProductsUseCase: GetSuggestedProductsUseCase,
    private val getTotalPriceInChartUseCase: GetTotalPriceInChartUseCase,
    private val deleteAllItemsInCartUseCase: DeleteAllItemsInCartUseCase
) : ViewModel() {

    private var _getProductsFromCart: MutableStateFlow<ViewState<BaseResponse<List<Product>>>> =
        MutableStateFlow(ViewState.Loading)
    val getProductsFromCart = _getProductsFromCart.asStateFlow()

    private var _getSuggestedProducts: MutableStateFlow<ViewState<BaseResponse<List<SuggestedProductResponse>>>> =
        MutableStateFlow(ViewState.Loading)
    val getSuggestedProducts = _getSuggestedProducts.asStateFlow()

    private var _getTotalPrice: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val getTotalPrice = _getTotalPrice.asStateFlow()

    fun getSuggestedProductsFromApi() {
        getSuggestedProductsUseCase().map { response ->
            when (response) {
                is BaseResponse.Success -> {
                    ViewState.Success(response)
                }

                is BaseResponse.Error -> {
                    ViewState.Error(response.message)
                }
            }
        }.onEach { data ->
            _getSuggestedProducts.emit(data)
        }.catch {
            _getSuggestedProducts.emit(ViewState.Error(it.message.toString()))
        }.launchIn(viewModelScope)
    }

    fun getProductsFromCart() {
        getProductsFromCartUseCase()?.map { response ->
            when (response) {
                is BaseResponse.Success -> {
                    ViewState.Success(response)
                }

                is BaseResponse.Error -> {
                    ViewState.Error(response.message)
                }
            }
        }?.onEach { data ->
            _getProductsFromCart.emit(data)
        }?.catch {
            _getProductsFromCart.emit(ViewState.Error(it.message.toString()))
        }?.launchIn(viewModelScope)
    }

    fun deleteAllItems() {
        viewModelScope.launch {
            deleteAllItemsInCartUseCase()
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
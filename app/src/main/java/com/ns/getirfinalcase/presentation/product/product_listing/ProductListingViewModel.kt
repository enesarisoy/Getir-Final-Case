package com.ns.getirfinalcase.presentation.product.product_listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.model.product.ProductResponse
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProductResponse
import com.ns.getirfinalcase.domain.usecase.product.local.AddToCartProductUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.DeleteFromCartUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetProductByIdUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.GetProductsFromCartUseCase
import com.ns.getirfinalcase.domain.usecase.product.local.UpdateProductsUseCase
import com.ns.getirfinalcase.domain.usecase.product.remote.GetAllProductUseCase
import com.ns.getirfinalcase.domain.usecase.product.remote.GetSuggestedProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
class ProductListingViewModel @Inject constructor(
    private val getAllProductUseCase: GetAllProductUseCase,
    private val getProductsFromCartUseCase: GetProductsFromCartUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartProductUseCase: AddToCartProductUseCase,
    private val deleteFromCartUseCase: DeleteFromCartUseCase,
    private val getSuggestedProductsUseCase: GetSuggestedProductsUseCase,
    private val updateProductsUseCase: UpdateProductsUseCase
) : ViewModel() {

    // Gets products from the API.
    private var _getAllProducts: MutableStateFlow<ViewState<BaseResponse<List<ProductResponse>>>> =
        MutableStateFlow(ViewState.Loading)
    val getAllProducts = _getAllProducts.asStateFlow()

    // Gets all products from the local database.
    private var _getProductsFromCart: MutableStateFlow<ViewState<BaseResponse<List<Product>>>> =
        MutableStateFlow(ViewState.Loading)
    val getProductsFromCart = _getProductsFromCart.asStateFlow()

    // Gets suggested products from the API.
    private var _getSuggestedProducts: MutableStateFlow<ViewState<BaseResponse<List<SuggestedProductResponse>>>> =
        MutableStateFlow(ViewState.Loading)
    val getSuggestedProducts = _getSuggestedProducts.asStateFlow()

    // Adds a product to the cart.
    private var _addToCart: MutableStateFlow<Product?> = MutableStateFlow(null)
    val addToCart = _addToCart.asStateFlow()

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

    fun getAllProductsFromApi() {
        viewModelScope.launch {
            _getAllProducts.value = ViewState.Loading

            delay(1800)

            try {
                getAllProductUseCase().map { response ->
                    when (response) {
                        is BaseResponse.Success -> {
                            ViewState.Success(response)
                        }

                        is BaseResponse.Error -> {
                            ViewState.Error(response.message)
                        }
                    }
                }.onEach { data ->
                    _getAllProducts.emit(data)
                }.launchIn(viewModelScope)
            } catch (e: Exception) {
                _getAllProducts.value = ViewState.Error(e.message.toString())
            }
        }
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

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val checkProductAlreadyInDatabase = getProductByIdUseCase(product.id).firstOrNull()

            checkProductAlreadyInDatabase?.let {
                it.quantity++
                updateProductsUseCase.invoke(it.id, it.quantity)
                _addToCart.value = it
            } ?: run {
                addToCartProductUseCase(product)
                _addToCart.value = product
            }
            getProductsFromCart()
        }
    }

    fun deleteFromCart(product: Product) {
        viewModelScope.launch {
            val checkProductAlreadyInDatabase = getProductByIdUseCase(product.id).firstOrNull()

            checkProductAlreadyInDatabase?.let {
                if (it.quantity > 1) {
                    it.quantity--
                    updateProductsUseCase.invoke(it.id, it.quantity)
                } else {
                    deleteFromCartUseCase(product.id)
                }
            }
            getProductsFromCart()
        }
    }
}
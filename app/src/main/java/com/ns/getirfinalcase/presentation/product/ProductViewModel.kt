package com.ns.getirfinalcase.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.domain.model.product.ProductResponse
import com.ns.getirfinalcase.domain.usecase.product.remote.GetAllProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getAllProductUseCase: GetAllProductUseCase
) : ViewModel() {

    private var _getAllProducts: MutableStateFlow<ViewState<BaseResponse<List<ProductResponse>>>> =
        MutableStateFlow(ViewState.Loading)
    val getAllProducts = _getAllProducts

    fun getAllProducts() {
        getAllProductUseCase().map {response ->
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
        }.catch {
            _getAllProducts.emit(ViewState.Error(it.message.toString()))
        }.launchIn(viewModelScope)
    }

}
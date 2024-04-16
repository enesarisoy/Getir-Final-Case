package com.ns.getirfinalcase.presentation.product.product_listing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.ns.getirfinalcase.R
import com.ns.getirfinalcase.core.base.BaseFragment
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.databinding.FragmentProductListingBinding
import com.ns.getirfinalcase.databinding.ItemProductListingBinding
import com.ns.getirfinalcase.databinding.ItemProductListingViewBinding
import com.ns.getirfinalcase.databinding.ItemToolbarProductsBinding
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.presentation.adapter.SingleRecyclerAdapter
import com.ns.getirfinalcase.presentation.product.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListingFragment : BaseFragment<FragmentProductListingBinding>(
    FragmentProductListingBinding::inflate
) {

    private val viewModel: ProductViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initAdapters()

        getProducts()
    }



    private val toolbarAdapter = SingleRecyclerAdapter<ItemToolbarProductsBinding, String>(
        { inflater, _, _ ->
            ItemToolbarProductsBinding.inflate(
                inflater,
                binding.rvProductListingScreen,
                false
            )
        },
        { binding, item ->
            binding.apply {
                ivCart.setOnClickListener {
                    findNavController().navigate(R.id.action_productListingFragment_to_shoppingCartFragment)
                }
            }
        }
    )

    private val productListingAdapter = SingleRecyclerAdapter<ItemProductListingBinding, String>(
        { inflater, _, _ ->
            ItemProductListingBinding.inflate(
                inflater,
                binding.rvProductListingScreen,
                false
            )
        },
        { binding, item ->
            binding.apply {
                rvItemProductListing.layoutManager = GridLayoutManager(binding.root.context, 3)
                rvItemProductListing.adapter = itemProductListingAdapter
            }
        }
    )

    private val itemProductListingAdapter = SingleRecyclerAdapter<ItemProductListingViewBinding, Product>(
        { inflater, _, _ ->
            ItemProductListingViewBinding.inflate(
                inflater,
                binding.rvProductListingScreen,
                false
            )
        },
        { binding, item ->
            binding.apply {
                tvProductName.text = item.name
                tvAttribute.text = item.attribute
                tvPrice.text = item.priceText

                ivAdd.setOnClickListener {

                }

            }
        }
    )

    private fun getProducts() {
        binding.apply {
            viewModel.getAllProducts()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getAllProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { viewState ->
                        when (viewState) {
                            is ViewState.Success -> {
//                                val fromBasket = mutableListOf<Product>()
//
//                                viewModel.productsFromBasket.observe(viewLifecycleOwner) {
//                                    it?.let { it1 -> fromBasket.addAll(it1) }
//                                }
                                val response = viewState.result as BaseResponse.Success
//                                fromBasket.forEach { basketProduct ->
//                                    if (basketProduct.quantity > 0) {
//                                        response.data[0].products.firstOrNull { it.id == basketProduct.id }
//                                            ?.let { product ->
//                                                product.quantity = basketProduct.quantity
//                                            }
//                                    }
//                                }

                                itemProductListingAdapter.data = response.data[0].products.toMutableList()
                                println(response.data.toString())


                            }

                            is ViewState.Error -> {
                                println(viewState.error.toString())

                            }

                            is ViewState.Loading -> {

                            }
                        }
                    }

            }
        }
    }


    private fun initListener() {
        binding.rvProductListingScreen.adapter =concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        toolbarAdapter,
        productListingAdapter
    )

    private fun initAdapters() {
        binding.apply {
            toolbarAdapter.data = listOf("AddressAdapter")
//            productListingAdapter.data = listOf(Product(id = "", price = 0.0))
            productListingAdapter.data = listOf("")
        }
    }
}
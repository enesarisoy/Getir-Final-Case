package com.ns.getirfinalcase.presentation.product.product_listing

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.ns.getirfinalcase.R
import com.ns.getirfinalcase.core.base.BaseFragment
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.core.util.gone
import com.ns.getirfinalcase.core.util.visible
import com.ns.getirfinalcase.databinding.FragmentProductListingBinding
import com.ns.getirfinalcase.databinding.ItemProductListingBinding
import com.ns.getirfinalcase.databinding.ItemProductListingViewBinding
import com.ns.getirfinalcase.databinding.ItemToolbarProductsBinding
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.presentation.adapter.SingleRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListingFragment : BaseFragment<FragmentProductListingBinding>(
    FragmentProductListingBinding::inflate
) {

    private val viewModel: ProductListingViewModel by viewModels()
    private val productsFromBasket = mutableListOf<Product>()
    private var totalPrice: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initAdapters()

        getProducts()
        getProductsFromCart()
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
                viewModel.getProductsFromCart()
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getProductsFromCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collect { viewState ->
                            when (viewState) {
                                is ViewState.Success -> {
                                    linearCart.visible()
                                    //TODO (Animate Cart)
                                    totalPrice = 0.0
                                    productsFromBasket.forEach {
                                        totalPrice += it.price * it.quantity

                                    }
                                    tvPrice.text =
                                        "₺${String.format("%.2f", totalPrice)}"

                                    if (totalPrice == 0.0) {
                                        //TODO (Animate Cart)

                                        linearCart.gone()
                                    }
                                }

                                is ViewState.Error -> {
                                    Log.d("Database", viewState.error)

                                }

                                is ViewState.Loading -> {
                                    Log.d("Database", "Loading")

                                }
                            }
                        }
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

    private val itemProductListingAdapter =
        SingleRecyclerAdapter<ItemProductListingViewBinding, Product>(
            { inflater, _, _ ->
                ItemProductListingViewBinding.inflate(
                    inflater,
                    binding.rvProductListingScreen,
                    false
                )
            },
            { binding, product ->
                binding.apply {
                    tvProductName.text = product.name
                    tvAttribute.text = product.attribute
                    tvPrice.text = product.priceText
                    Glide.with(binding.root.context).load(product.imageURL).into(ivFood)

                    if (product.quantity > 0) {
                        tvProductQuantity.text = product.quantity.toString()
                        displayComponents(binding)
                    } else {
                        clearComponents(binding)
                    }


                    ivAdd.setOnClickListener {
                        displayComponents(binding)
                        product.quantity++
                        viewModel.addToCart(product)
                        ivDelete.isClickable = true

                        tvProductQuantity.text = product.quantity.toString()

                    }

                    ivDelete.setOnClickListener {
                        product.quantity--

                        viewModel.deleteFromCart(product)
                        tvProductQuantity.text = product.quantity.toString()

                        if (product.quantity == 0) {
                            ivDelete.isClickable = false
                            clearComponents(binding)

                        }

                    }


                }
            }
        )


    private fun displayComponents(binding: ItemProductListingViewBinding) {
        binding.apply {

            linearLayout.visible()
            ivAdd.background =
                ResourcesCompat.getDrawable(
                    root.resources,
                    R.drawable.cart_add_icon_bg_when_clicked,
                    null
                )
            tvProductQuantity.setBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    R.color.bg_primary
                )
            )
            ivDelete.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    R.drawable.ic_thrash
                )
            )
        }
    }

    private fun clearComponents(binding: ItemProductListingViewBinding) {
        binding.apply {
            linearLayout.gone()
            tvProductQuantity.setBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    R.color.white
                )
            )
            ivAdd.background =
                ResourcesCompat.getDrawable(
                    root.resources,
                    R.drawable.cart_add_icon_background,
                    null
                )
            tvProductQuantity.text = null
            ivDelete.setImageDrawable(null)

        }
    }

    private fun getProductsFromCart() {
        binding.apply {
            viewModel.getProductsFromCart()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getProductsFromCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { viewState ->
                        when (viewState) {
                            is ViewState.Success -> {
                                val response = viewState.result as BaseResponse.Success
                                productsFromBasket.clear()
                                productsFromBasket.addAll(response.data)
                                Log.d("Database", response.data.toString())

                            }

                            is ViewState.Error -> {
                                Log.d("Database", viewState.error)

                            }

                            is ViewState.Loading -> {
                                Log.d("Database", "Loading")

                            }
                        }
                    }
            }
        }
    }

    private fun getProducts() {
        binding.apply {
            viewModel.getAllProductsFromApi()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getAllProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { viewState ->
                        when (viewState) {
                            is ViewState.Success -> {

                                val response = viewState.result as BaseResponse.Success
                                productsFromBasket.forEach { basketProduct ->
                                    if (basketProduct.quantity > 0) {
                                        response.data[0].products.firstOrNull { it.id == basketProduct.id }
                                            ?.let { product ->
                                                product.quantity = basketProduct.quantity
                                            }
                                    }
                                }


                                itemProductListingAdapter.data = response.data[0].products

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
        binding.rvProductListingScreen.adapter = concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        toolbarAdapter,
        productListingAdapter
    )

    private fun initAdapters() {
        binding.apply {
            toolbarAdapter.data = listOf("toolbarAdapter")
            productListingAdapter.data = listOf("productListingAdapter")
        }
    }
}
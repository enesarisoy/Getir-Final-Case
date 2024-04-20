package com.ns.getirfinalcase.presentation.shopping_cart

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.bumptech.glide.Glide
import com.ns.getirfinalcase.R
import com.ns.getirfinalcase.core.base.BaseFragment
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.core.util.gone
import com.ns.getirfinalcase.core.util.visible
import com.ns.getirfinalcase.data.mapper.toProduct
import com.ns.getirfinalcase.data.mapper.toSuggestedProduct
import com.ns.getirfinalcase.databinding.FragmentShoppingCartBinding
import com.ns.getirfinalcase.databinding.ItemProductListingViewBinding
import com.ns.getirfinalcase.databinding.ItemShoppingCartProductsBinding
import com.ns.getirfinalcase.databinding.ItemShoppingCartProductsViewBinding
import com.ns.getirfinalcase.databinding.ItemShoppingCartSuggestedProductsBinding
import com.ns.getirfinalcase.databinding.ItemShoppingCartSuggestedProductsViewBinding
import com.ns.getirfinalcase.databinding.ItemTitleBinding
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProduct
import com.ns.getirfinalcase.presentation.adapter.SingleRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCartFragment : BaseFragment<FragmentShoppingCartBinding>(
    FragmentShoppingCartBinding::inflate
) {

    private val viewModel: ShoppingCartViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initAdapters()
        getProductsInCart()
        getSuggestedProductsFromApi()
        checkCartPrice()

        deleteAllItems()


    }

    private fun deleteAllItems() {
        // TODO(anim)
        with(binding) {
            toolbarShoppingCart.ivDelete.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                    .create()
                val view = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
                val btnPositive = view.findViewById<Button>(R.id.btnPositive)
                val btnNegative = view.findViewById<Button>(R.id.btnNegative)
                builder.setView(view)
                btnNegative.setOnClickListener {
                    builder.dismiss()
                }
                btnPositive.setOnClickListener {
                    viewModel.deleteAllItems()
                    builder.dismiss()
                }
                builder.setCanceledOnTouchOutside(false)
                builder.show()
            }
        }
    }

    private fun checkCartPrice() {
        with(binding) {
            viewModel.getTotalPrice()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getTotalPrice.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { totalPrice ->
                        layoutContinue.tvPrice.text = "₺${String.format("%.2f", totalPrice)}"
                    }
            }
        }
    }

    private fun getProductsInCart() {
        binding.apply {
            viewModel.getProductsFromCart()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getProductsFromCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { viewState ->
                        when (viewState) {
                            is ViewState.Success -> {
                                val response = viewState.result as BaseResponse.Success
                                itemProductsInCartAdapter.data = response.data
                                if (response.data.isEmpty()) {
                                    findNavController().navigate(R.id.action_shoppingCartFragment_to_productListingFragment)
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

    private fun getSuggestedProductsFromApi() {
        binding.apply {
            viewModel.getSuggestedProductsFromApi()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getSuggestedProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { viewState ->
                        when (viewState) {
                            is ViewState.Success -> {
                                val response = viewState.result as BaseResponse.Success

                                itemSuggestedProductsAdapter.data = response.data[0].products
                            }

                            is ViewState.Error -> {
                                Log.d("SuggestedProducts", viewState.error)
                            }

                            is ViewState.Loading -> {
                                Log.d("SuggestedProducts", "Loading")
                            }
                        }
                    }
            }
        }
    }

    private val productsInCartAdapter =
        SingleRecyclerAdapter<ItemShoppingCartProductsBinding, String>(
            { inflater, _, _ ->
                ItemShoppingCartProductsBinding.inflate(
                    inflater,
                    binding.rvShoppingCartScreen,
                    false
                )
            },
            { binding, item ->
                binding.apply {
                    rvItemShoppingCartProducts.adapter = itemProductsInCartAdapter
                }
            }
        )

    private val itemProductsInCartAdapter =
        SingleRecyclerAdapter<ItemShoppingCartProductsViewBinding, Product>(
            { inflater, _, _ ->
                ItemShoppingCartProductsViewBinding.inflate(
                    inflater,
                    binding.rvShoppingCartScreen,
                    false
                )
            },
            { binding, product ->

                binding.apply {
                    tvFoodName.text = product.name
                    tvAttribute.text = product.attribute
                    tvPrice.text = product.priceText
                    tvProductQuantity.text = product.quantity.toString()
                    Glide.with(binding.root.context).load(product.imageURL).into(ivFood)


                    ivAdd.setOnClickListener {
                        product.quantity++
                        viewModel.addToCart(product)
                        tvProductQuantity.text = product.quantity.toString()

                    }

                    ivDelete.setOnClickListener {
                        product.quantity--

                        viewModel.deleteFromCart(product)
                        tvProductQuantity.text = product.quantity.toString()

                    }

                    root.setOnClickListener {
                        findNavController().navigate(
                            ShoppingCartFragmentDirections.actionShoppingCartFragmentToProductDetailFragment(
                                product
                            )
                        )
                    }
                }

            }
        )

    private val itemTitleAdapter =
        SingleRecyclerAdapter<ItemTitleBinding, String>(
            { inflater, _, _ ->
                ItemTitleBinding.inflate(
                    inflater,
                    binding.rvShoppingCartScreen,
                    false
                )
            },
            { binding, item ->
                binding.apply {
                    tvTitle.text = getString(R.string.recommended_products)
                }
            }
        )

    private val suggestedProductsAdapter =
        SingleRecyclerAdapter<ItemShoppingCartSuggestedProductsBinding, String>(
            { inflater, _, _ ->
                ItemShoppingCartSuggestedProductsBinding.inflate(
                    inflater,
                    binding.rvShoppingCartScreen,
                    false
                )
            },
            { binding, item ->
                binding.apply {
                    rvItemShoppingCartSuggestedProducts.adapter = itemSuggestedProductsAdapter
                }
            }
        )
    // TODO DELETE PROBLEM

    private val itemSuggestedProductsAdapter =
        SingleRecyclerAdapter<ItemProductListingViewBinding, SuggestedProduct>(
            { inflater, _, _ ->
                ItemProductListingViewBinding.inflate(
                    inflater,
                    binding.rvShoppingCartScreen,
                    false
                )
            },
            { binding, suggestedProduct ->
                val product = suggestedProduct.toProduct()

                binding.apply {
                    tvProductName.text = suggestedProduct.name
                    tvPrice.text = suggestedProduct.priceText
                    Glide.with(binding.root.context)
                        .load(suggestedProduct.imageURL ?: suggestedProduct.squareThumbnailURL)
                        .into(ivFood)

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.addToCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                            .collect { productResponse ->
                                productResponse?.let {
                                    if (productResponse.id == product.id) {
                                        tvProductQuantity.text = productResponse.quantity.toString()
                                    }
                                }
                            }
                    }

                    ivAdd.setOnClickListener {
                        product.quantity++
                        viewModel.addToCart(product)
                    }

                    root.setOnClickListener {
                        findNavController().navigate(
                            ShoppingCartFragmentDirections.actionShoppingCartFragmentToProductDetailFragment(
                                suggestedProduct.toProduct().copy(quantity = 0)
                            )
                        )
                    }
                }

            }
        )

    private fun initListener() {
        binding.rvShoppingCartScreen.adapter = concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        productsInCartAdapter,
        itemTitleAdapter,
        suggestedProductsAdapter
    )

    private fun initAdapters() {
        binding.apply {
            productsInCartAdapter.data = listOf("productsInCartAdapter")
            itemTitleAdapter.data = listOf("itemTitle")
            suggestedProductsAdapter.data = listOf("suggestedProductsAdapter")
        }
    }
}

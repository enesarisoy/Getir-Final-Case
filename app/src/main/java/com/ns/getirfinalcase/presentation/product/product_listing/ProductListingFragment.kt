package com.ns.getirfinalcase.presentation.product.product_listing

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
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
import com.ns.getirfinalcase.core.util.animateCart
import com.ns.getirfinalcase.core.util.gone
import com.ns.getirfinalcase.core.util.showToast
import com.ns.getirfinalcase.core.util.visible
import com.ns.getirfinalcase.data.mapper.toProduct
import com.ns.getirfinalcase.databinding.FragmentProductListingBinding
import com.ns.getirfinalcase.databinding.ItemProductListingBinding
import com.ns.getirfinalcase.databinding.ItemProductListingViewBinding
import com.ns.getirfinalcase.databinding.ItemShoppingCartSuggestedProductsBinding
import com.ns.getirfinalcase.domain.model.product.Product
import com.ns.getirfinalcase.domain.model.suggested_product.SuggestedProduct
import com.ns.getirfinalcase.presentation.adapter.SingleRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListingFragment : BaseFragment<FragmentProductListingBinding>(
    FragmentProductListingBinding::inflate
) {

    private val viewModel: ProductListingViewModel by activityViewModels()
    private val productsFromBasket = mutableListOf<Product>()
    private var totalPrice: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initAdapters()

        getProducts()
        getProductsFromCart()

        getSuggestedProducts()

    }

    private val suggestedProductsAdapter =
        SingleRecyclerAdapter<ItemShoppingCartSuggestedProductsBinding, String>(
            { inflater, _, _ ->
                ItemShoppingCartSuggestedProductsBinding.inflate(
                    inflater,
                    binding.rvProductListingScreen,
                    false
                )
            },
            { binding, _ ->
                binding.apply {
                    rvItemShoppingCartSuggestedProducts.adapter = itemSuggestedProductsAdapter
                }
            }
        )

    private val itemSuggestedProductsAdapter =
        SingleRecyclerAdapter<ItemProductListingViewBinding, SuggestedProduct>(
            { inflater, _, _ ->
                ItemProductListingViewBinding.inflate(
                    inflater,
                    binding.rvProductListingScreen,
                    false
                )
            },
            { binding, suggestedProduct ->

                val product = suggestedProduct.toProduct()
                binding.apply {
                    var isFirstTime: Boolean = true

                    tvProductName.text = suggestedProduct.name
                    tvPrice.text = suggestedProduct.priceText
                    Glide.with(binding.root.context)
                        .load(suggestedProduct.imageURL ?: suggestedProduct.squareThumbnailURL)
                        .into(ivFood)

                    productsFromBasket.firstOrNull { it.id == suggestedProduct.id }?.let {
                        product.quantity = it.quantity
                    }

                    if (product.quantity > 0) {
                        isFirstTime = false
                        tvProductQuantity.text = product.quantity.toString()
                        displayComponents(binding)
                    } else {
                        clearComponents(binding)
                    }

                    ivAdd.setOnClickListener {

                        product.quantity++
                        viewModel.addToCart(product)
                        displayComponents(binding)
                        ivDelete.isClickable = true

                        tvProductQuantity.text = product.quantity.toString()
                        if (isFirstTime) {
                            downToAnimation(binding)
                            isFirstTime = false
                        }
                    }

                    ivDelete.setOnClickListener {
                        product.quantity--

                        viewModel.deleteFromCart(product)
                        tvProductQuantity.text = product.quantity.toString()

                        if (product.quantity == 0) {
                            isFirstTime = true
                            ivDelete.isClickable = false
                            clearComponents(binding)
                            upToAnimation(binding)

                        }

                    }

                    root.setOnClickListener {
                        findNavController().navigate(
                            ProductListingFragmentDirections.actionProductListingFragmentToProductDetailFragment(
                                product
                            )
                        )
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
        { binding, _ ->
            binding.apply {
                rvItemProductListing.apply {
                    layoutManager = GridLayoutManager(binding.root.context, 3)
                    adapter = itemProductListingAdapter
                }
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
                    var isFirstTime: Boolean = true

                    tvProductName.text = product.name
                    tvAttribute.text = product.attribute
                    tvPrice.text = product.priceText
                    Glide.with(binding.root.context).load(product.imageURL).into(ivFood)

                    if (product.quantity > 0) {
                        isFirstTime = false
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

                        if (isFirstTime) {
                            downToAnimation(binding)
                            isFirstTime = false
                        }

                    }

                    ivDelete.setOnClickListener {
                        product.quantity--

                        viewModel.deleteFromCart(product)
                        tvProductQuantity.text = product.quantity.toString()

                        if (product.quantity == 0) {
                            isFirstTime = true
                            ivDelete.isClickable = false
                            clearComponents(binding)
                            upToAnimation(binding)

                        }

                    }

                    root.setOnClickListener {
                        findNavController().navigate(
                            ProductListingFragmentDirections.actionProductListingFragmentToProductDetailFragment(
                                product
                            )
                        )

                    }
                }

            }
        )

    private fun getProducts() {
        binding.apply {
            viewModel.getAllProductsFromApi()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getAllProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { viewState ->
                        when (viewState) {
                            is ViewState.Success -> {

                                progressBar.root.gone()
                                rvProductListingScreen.visible()
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
                            }

                            is ViewState.Error -> {
                                requireContext().showToast(viewState.error)
                            }

                            is ViewState.Loading -> {
                                progressBar.root.visible()
                                rvProductListingScreen.gone()
                            }
                        }
                    }
            }
        }
    }

    private fun getProductsFromCart() {
        binding.toolbarProductListing.apply {
            linearCart.setOnClickListener {
                findNavController().navigate(R.id.action_productListingFragment_to_shoppingCartFragment)
            }
            viewModel.getProductsFromCart()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getProductsFromCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { viewState ->
                        when (viewState) {
                            is ViewState.Success -> {
                                val response = viewState.result as BaseResponse.Success
                                productsFromBasket.clear()
                                productsFromBasket.addAll(response.data)

                                linearCart.visible()
                                animateCart(
                                    binding.toolbarProductListing.linearCart,
                                    150f,
                                    0f,
                                    visibility = View.VISIBLE
                                )

                                totalPrice = 0.0
                                productsFromBasket.forEach {
                                    totalPrice += it.price * it.quantity

                                }
                                tvPrice.text = getString(
                                    R.string.total_price,
                                    String.format("%.2f", totalPrice)
                                )

                                if (totalPrice == 0.0) {
                                    animateCart(
                                        binding.toolbarProductListing.linearCart,
                                        0f,
                                        280f,
                                        visibility = View.GONE
                                    )

                                }
                            }

                            is ViewState.Error -> {
                                requireContext().showToast(viewState.error)
                            }

                            is ViewState.Loading -> {
                                Log.d("Database", "Loading")
                            }
                        }
                    }
            }
        }
    }

    private fun getSuggestedProducts() {
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
                                requireContext().showToast(viewState.error)
                            }

                            is ViewState.Loading -> {
                                Log.d("SuggestedProducts", "Loading")
                            }
                        }
                    }
            }
        }
    }


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

    private fun downToAnimation(binding: ItemProductListingViewBinding) {
        performScaleAnimation(binding, 0f, 1f)
    }

    private fun upToAnimation(binding: ItemProductListingViewBinding) {
        performScaleAnimation(binding, 1f, 0f)
    }

    private fun performScaleAnimation(
        binding: ItemProductListingViewBinding,
        fromScaleY: Float,
        toScaleY: Float
    ) {
        val scaleAnimation = ScaleAnimation(
            1f,
            1f,
            fromScaleY,
            toScaleY,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0f
        )
        scaleAnimation.duration = 300
        scaleAnimation.interpolator = AccelerateInterpolator()

        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                binding.ivAdd.background =
                    ResourcesCompat.getDrawable(
                        binding.root.resources,
                        R.drawable.cart_add_icon_bg_when_clicked,
                        null
                    )
            }

            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (toScaleY == 1f) {
                    binding.tvProductQuantity.text = 1.toString()
                } else {
                    binding.ivAdd.background =
                        ResourcesCompat.getDrawable(
                            binding.root.resources,
                            R.drawable.cart_add_icon_background,
                            null
                        )
                }
            }
        })

        binding.linearLayout.startAnimation(scaleAnimation)
    }

    private fun initListener() {
        binding.rvProductListingScreen.adapter = concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        suggestedProductsAdapter,
        productListingAdapter
    )

    private fun initAdapters() {
        binding.apply {
            productListingAdapter.data = listOf("productListingAdapter")
            suggestedProductsAdapter.data = listOf("suggestedProductsAdapter")
        }
    }

}
package com.ns.getirfinalcase.presentation.product.product_detail

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ns.getirfinalcase.R
import com.ns.getirfinalcase.core.base.BaseFragment
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.core.util.gone
import com.ns.getirfinalcase.core.util.showToast
import com.ns.getirfinalcase.core.util.visible
import com.ns.getirfinalcase.databinding.FragmentProductDetailBinding
import com.ns.getirfinalcase.domain.model.product.Product
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::inflate
) {

    private val args: ProductDetailFragmentArgs by navArgs()
    private val viewModel: ProductDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initData()
        initClick()

        checkCartPrice()
        checkItemInCart()
        addItemToCart()

    }

    private fun initClick() {
        with(binding) {
            toolbarProductDetail.ivClose.setOnClickListener {
                findNavController().navigateUp()
            }
            toolbarProductDetail.linearCart.setOnClickListener {
                findNavController().navigate(R.id.action_productDetailFragment_to_shoppingCartFragment)
            }
        }
    }

    private fun initData() {
        with(binding) {
            tvProductName.text = args.product.name
            tvFoodPrice.text = args.product.priceText
            tvAttribute.text = args.product.attribute
            Glide.with(requireContext()).load(args.product.imageURL).into(ivFood)
        }
    }

    private fun addItemToCart() {
        with(binding) {
            btnAddToBasket.setOnClickListener {
                ivDelete.isClickable = true
                increaseProductQuantity(args.product)
            }
            ivAdd.setOnClickListener {
                increaseProductQuantity(args.product)
            }
            ivDelete.setOnClickListener {
                decreaseProductQuantity(args.product)
            }
        }
    }

    private fun increaseProductQuantity(product: Product) {
        viewModel.addToCart(product)

        observeCartChanges()
    }

    private fun decreaseProductQuantity(product: Product) {
        if (product.quantity == 0) {
            resetCartUI()
        } else {
            viewModel.deleteFromCart(product)
        }
        observeCartChanges()
    }

    private fun observeCartChanges() {
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.addToCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { response ->
                        when (response) {
                            is ViewState.Success -> {
                                val product = response.result as BaseResponse.Success

                                ivDelete.isClickable = true
                                ivAdd.isClickable = true

                                if (product.data != null) {
                                    updateCartUI(product.data)
                                } else {
                                    resetCartUI()
                                }

                            }

                            is ViewState.Error -> {
                                requireContext().showToast(response.error)
                            }

                            is ViewState.Loading -> {
                                linearLayout.visible()
                                btnAddToBasket.gone()
                                animationView.visible()
                                ivDelete.isClickable = false
                                ivAdd.isClickable = false
                            }
                        }
                    }
            }
        }
    }

    private fun updateCartUI(product: Product) {
        with(binding) {
            linearLayout.visible()
            btnAddToBasket.gone()
            tvProductQuantity.visible()
            animationView.gone()
            tvProductQuantity.text = product.quantity.toString()
        }
    }

    private fun resetCartUI() {
        with(binding) {
            linearLayout.gone()
            btnAddToBasket.visible()
            tvProductQuantity.text = "0"
        }
    }

    private fun checkItemInCart() {
        with(binding) {
            viewModel.getProductById(args.product)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getProductById.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { response ->
                        when (response) {
                            is ViewState.Success -> {
                                val product = response.result as BaseResponse.Success
                                progressBar.gone()

                                if (product.data != null) {
                                    linearLayout.visible()
                                    btnAddToBasket.gone()
                                    tvProductQuantity.text = product.data.quantity.toString()
                                } else {
                                    btnAddToBasket.visible()
                                    linearLayout.gone()
                                }
                            }

                            is ViewState.Error -> {
                                requireContext().showToast(response.error)
                            }

                            is ViewState.Loading -> {
                                progressBar.visible()
                                btnAddToBasket.visible()
                                animationView.gone()
                                linearLayout.gone()
                            }
                        }
                    }
            }
        }
    }

    private fun checkCartPrice() {
        viewModel.getTotalPrice()
        observeTotalPriceChanges()
    }

    private fun observeTotalPriceChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getTotalPrice.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { totalPrice ->
                    updateCartUI(totalPrice)
                }
        }
    }

    private fun updateCartUI(totalPrice: Double) {
        with(binding.toolbarProductDetail) {
            linearCart.visible()
            animateCart(150f, 0f)
            tvCartPrice.text = getString(R.string.total_price, String.format("%.2f", totalPrice))
            if (totalPrice == 0.0) {
                linearCart.gone()
                animateCart(0f, 280f)
            }
        }
    }

    private fun animateCart(from: Float, to: Float) {
        ObjectAnimator.ofFloat(binding.toolbarProductDetail.linearCart, "translationX", from, to)
            .apply {
                duration = 1000
                interpolator = OvershootInterpolator()
                start()
            }
    }
}
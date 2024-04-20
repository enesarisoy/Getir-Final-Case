package com.ns.getirfinalcase.presentation.product.product_detail

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ns.getirfinalcase.R
import com.ns.getirfinalcase.core.base.BaseFragment
import com.ns.getirfinalcase.core.util.gone
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
    private val viewModel: ProductDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("Argument: ${args.product}")

        initData()
        initClick()

        checkCartPrice()
        checkItemInCart()
        addItemToCart()
    }
    // TODO ANIMATION

    private fun addItemToCart() {
        with(binding) {
            btnAddToBasket.setOnClickListener {
                args.product.quantity++
                ivDelete.isClickable = true
                viewModel.addToCart(args.product)
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.addToCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collect { product ->
                            product?.let {
                                linearLayout.visible()
                                btnAddToBasket.gone()
                                tvProductQuantity.text = product.quantity.toString()
                            }
                        }
                }
            }
            ivAdd.setOnClickListener {
                args.product.quantity++
                viewModel.addToCart(args.product)
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.addToCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collect { product ->
                            product?.let {
                                tvProductQuantity.text = product.quantity.toString()
                            }
                        }
                }
            }
            ivDelete.setOnClickListener {
                args.product.quantity--

                viewModel.deleteFromCart(args.product)
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.addToCart.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collect { product ->
                            product?.let {
                                ivDelete.isClickable = true
                                linearLayout.visible()
                                btnAddToBasket.gone()
                                tvProductQuantity.text = product.quantity.toString()
                            } ?: run {
                                ivDelete.isClickable = false
                                linearLayout.gone()
                                btnAddToBasket.visible()
                                tvProductQuantity.text = 0.toString()
                            }
                        }
                }
            }
        }
    }


    private fun checkItemInCart() {
        with(binding) {

            viewModel.getProductById(args.product)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getProductById.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { product ->
                        product?.let {
                            linearLayout.visible()
                            btnAddToBasket.gone()
                            tvProductQuantity.text = product.quantity.toString()
                        }
                    }
            }
        }
    }

    private fun checkCartPrice() {
        with(binding) {
            viewModel.getTotalPrice()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getTotalPrice.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { totalPrice ->
                        toolbarProductDetail.linearCart.visible()
                        animateCart(150f, 0f)
                        toolbarProductDetail.tvCartPrice.text =
                            "₺${String.format("%.2f", totalPrice)}"
                        if (totalPrice == 0.0) {
                            toolbarProductDetail.linearCart.gone()
                            animateCart(0f, 280f)
                        }
                    }
            }
        }
    }

    private fun initClick() {
        with(binding) {
            toolbarProductDetail.ivClose.setOnClickListener {
//                findNavController().navigateUp()TODO
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

    private fun animateCart(from: Float, to: Float) {
        ObjectAnimator.ofFloat(binding.toolbarProductDetail.linearCart, "translationX", from, to).apply {
            duration = 1000
            interpolator = OvershootInterpolator()
            start()
        }

    }
}
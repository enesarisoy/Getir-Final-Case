package com.ns.getirfinalcase.presentation.product.product_detail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ns.getirfinalcase.core.base.BaseFragment
import com.ns.getirfinalcase.databinding.FragmentProductDetailBinding
import com.ns.getirfinalcase.domain.model.product.Product
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::inflate
) {

    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("Argument: ${args.product}")

        initData()
    }

    private fun initData() {
        with(binding) {
            tvProductName.text = args.product.name
            tvFoodPrice.text = args.product.priceText
            tvAttribute.text = args.product.attribute
            Glide.with(requireContext()).load(args.product.imageURL).into(ivFood)
        }
    }
}
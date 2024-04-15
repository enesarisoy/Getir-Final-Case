package com.ns.getirfinalcase

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ns.getirfinalcase.core.base.BaseResponse
import com.ns.getirfinalcase.core.domain.ViewState
import com.ns.getirfinalcase.databinding.ActivityMainBinding
import com.ns.getirfinalcase.presentation.product.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getAllProducts()
        lifecycleScope.launch {
            viewModel.getAllProducts.flowWithLifecycle(lifecycle)
                .collect { viewState ->
                    when (viewState) {
                        is ViewState.Success -> {
                            val response = viewState.result as BaseResponse.Success

                            Log.d("Products", response.data[0].products.toString())
                        }

                        is ViewState.Error -> {
                            Log.d("Products", "Error: ${viewState.error}")
                        }

                        is ViewState.Loading -> {
                            Log.d("Products", "Loading..")
                        }
                    }
                }

        }

    }
}
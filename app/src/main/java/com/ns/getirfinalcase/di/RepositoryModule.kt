package com.ns.getirfinalcase.di

import com.ns.getirfinalcase.data.repository.remote.ProductRepositoryImpl
import com.ns.getirfinalcase.data.source.remote.service.ProductService
import com.ns.getirfinalcase.domain.repository.remote.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        productService: ProductService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): ProductRepository =
        ProductRepositoryImpl(productService, dispatcher)
}
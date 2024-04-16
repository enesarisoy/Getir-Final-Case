package com.ns.getirfinalcase.di

import android.content.Context
import androidx.room.Room
import com.ns.getirfinalcase.data.source.local.ProductDatabase
import com.ns.getirfinalcase.data.source.local.ProductsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideProductDatabase(@ApplicationContext context: Context): ProductDatabase {
        return Room.databaseBuilder(context, ProductDatabase::class.java, "products.db")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideProductsDao(productDatabase: ProductDatabase): ProductsDao {
        return productDatabase.productsDao()
    }
}
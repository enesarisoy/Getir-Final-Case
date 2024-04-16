package com.ns.getirfinalcase.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ns.getirfinalcase.domain.model.product.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDao
}
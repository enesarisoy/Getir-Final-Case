package com.ns.getirfinalcase.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ns.getirfinalcase.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(product: Product)

    @Query("SELECT * FROM product")
    fun getProductsFromCart(): Flow<List<Product>>?

    @Query("SELECT * FROM product WHERE id = :productId")
    fun getProductById(productId: String): Flow<Product?>

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun deleteFromCart(id: String)

    @Query("DELETE FROM product")
    suspend fun deleteAllItemsInCart()

}
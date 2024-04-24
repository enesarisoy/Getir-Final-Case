package com.ns.getirfinalcase.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT IFNULL(SUM(quantity * price), 0) FROM product")
    fun getTotalPriceInCart(): Flow<Double?>

    @Query("UPDATE product SET quantity = :newQuantity WHERE id = :productId")
    suspend fun updateQuantity(productId: String, newQuantity: Int)

}
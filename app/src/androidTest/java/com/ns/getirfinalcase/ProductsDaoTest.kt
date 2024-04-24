package com.ns.getirfinalcase

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ns.getirfinalcase.data.source.local.ProductDatabase
import com.ns.getirfinalcase.data.source.local.ProductsDao
import com.ns.getirfinalcase.domain.model.product.Product
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductsDaoTest {

    private lateinit var productDatabase: ProductDatabase
    private lateinit var productsDao: ProductsDao

    @Before
    fun setup() {
        productDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProductDatabase::class.java
        ).allowMainThreadQueries().build()
        productsDao = productDatabase.productsDao()

    }

    @After
    fun tearDown() {
        productDatabase.close()
    }

    @Test
    fun testAddToCart() {
        runBlocking {
            val product = Product(
                id = "6540f93a48e4bd7bf4940ffd",
                price = 140.75,
                name = "Master Nut NR1 Mixed Nuts",
                imageURL = "https://market-product-images-cdn.getirapi.com/product/dee83b80-7f9a-4aea-b799-e3316b5696f1.jpg",
                priceText = "₺140,75",
                shortDescription = "140 g"
            )

            productsDao.addToCart(product)

            val retrievedProduct = productsDao.getProductById(product.id).firstOrNull()

            assertNotNull(retrievedProduct)

            assertEquals(product.id, retrievedProduct?.id)
            assertEquals(product.id, retrievedProduct?.id)
            retrievedProduct?.price?.let { assertEquals(product.price, it, 0.001) }
            assertEquals(product.name, retrievedProduct?.name)
            assertEquals(product.imageURL, retrievedProduct?.imageURL)
            assertEquals(product.priceText, retrievedProduct?.priceText)
            assertEquals(product.shortDescription, retrievedProduct?.shortDescription)
        }
    }

    @Test
    fun testGetProductsFromCart() = runBlocking {
        val products = listOf(
            Product(id = "1", price = 10.0),
            Product(id = "2", price = 15.0),
            Product(id = "3", price = 20.0)
        )

        products.forEach { product ->
            productsDao.addToCart(product)
        }

        val productsInCart = productsDao.getProductsFromCart()?.firstOrNull()

        assertEquals(products.size, productsInCart?.size ?: 0)
        products.forEachIndexed { index, product ->
            assertEquals(product.id, productsInCart?.getOrNull(index)?.id)
            productsInCart?.getOrNull(index)?.price?.let { assertEquals(product.price, it, 0.001) }
        }
    }

    @Test
    fun testGetProductById() {
        runBlocking {
            val product = Product(
                id = "6540f93a48e4bd7bf4940ffd",
                price = 140.75,
                name = "Master Nut NR1 Mixed Nuts",
                imageURL = "https://market-product-images-cdn.getirapi.com/product/dee83b80-7f9a-4aea-b799-e3316b5696f1.jpg",
                priceText = "₺140,75",
                shortDescription = "140 g"
            )

            productsDao.addToCart(product)

            val retrievedProduct = productsDao.getProductById(product.id).firstOrNull()

            assertEquals(product.id, retrievedProduct?.id)
            retrievedProduct?.price?.let { assertEquals(product.price, it, 0.001) }
        }
    }

    @Test
    fun testGetTotalPriceInCart() = runBlocking {
        val products = listOf(
            Product(id = "1", price = 10.0, quantity = 2),
            Product(id = "2", price = 15.0, quantity = 1),
            Product(id = "3", price = 20.0, quantity = 3)
        )

        products.forEach { product ->
            productsDao.addToCart(product)
        }

        val totalPrice = productsDao.getTotalPriceInCart().firstOrNull()

        val expectedTotalPrice = products.sumOf { it.price * it.quantity }
        assertEquals(expectedTotalPrice, totalPrice ?: 0.0, 0.001)
    }


    @Test
    fun testUpdateQuantity() = runBlocking {
        val productId = "1"
        val initialQuantity = 1
        val updatedQuantity = 3

        val product = Product(
            id = productId,
            price = 10.0,
            quantity = initialQuantity
        )

        productsDao.addToCart(product)

        productsDao.updateQuantity(productId, updatedQuantity)

        val updatedProduct = productsDao.getProductById(productId).firstOrNull()
        assertEquals(updatedQuantity, updatedProduct?.quantity)
    }

    @Test
    fun testDeleteAllItemsInCart() = runBlocking {
        val products = listOf(
            Product(id = "1", price = 10.0),
            Product(id = "2", price = 15.0),
            Product(id = "3", price = 20.0)
        )

        products.forEach { product ->
            productsDao.addToCart(product)
        }

        var itemsInCart = productsDao.getProductsFromCart()?.firstOrNull()
        TestCase.assertEquals(products.size, itemsInCart?.size ?: 0)

        productsDao.deleteAllItemsInCart()

        itemsInCart = productsDao.getProductsFromCart()?.firstOrNull()
        TestCase.assertEquals(0, itemsInCart?.size ?: 0)
    }

}
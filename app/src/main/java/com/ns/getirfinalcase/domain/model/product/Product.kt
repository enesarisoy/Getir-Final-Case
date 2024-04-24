package com.ns.getirfinalcase.domain.model.product

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "product")
@Parcelize
data class Product(
    val attribute: String? = null,
    val id: String,
    val imageURL: String? = null,
    val name: String? = null,
    val price: Double,
    val priceText: String? = null,
    val shortDescription: String? = null,
    val thumbnailURL: String? = null,
    var quantity: Int = 1
): Parcelable{
    @PrimaryKey(autoGenerate = true)
    var dbId:Int = 0
}
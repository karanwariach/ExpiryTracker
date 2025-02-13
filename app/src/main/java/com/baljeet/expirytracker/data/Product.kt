package com.baljeet.expirytracker.data

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var productId: Int,
    var name: String,
    var categoryId: Int,
    var imageId: Int,
    var isDeleted : Boolean
):Parcelable

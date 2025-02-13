package com.baljeet.expirytracker.data.relations

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.Product
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ProductAndCategoryAndImage(
    @Embedded val product : Product,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId",
        entity = Category::class
    )
    val categoryAndImage : CategoryAndImage,
    @Relation(
        parentColumn = "imageId",
        entityColumn = "imageId"
    )
    val image: Image
): Parcelable

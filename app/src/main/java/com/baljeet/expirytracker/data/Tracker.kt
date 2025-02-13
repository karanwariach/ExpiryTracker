package com.baljeet.expirytracker.data

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.LocalDateTime

@Keep
@Parcelize
@Entity(tableName = "Tracker")
data class Tracker(
    @PrimaryKey(autoGenerate = true)
    var trackerId: Int = 0,
    var productId: Int,
    var mfgDate: @RawValue LocalDateTime?,
    var expiryDate: @RawValue LocalDateTime?,
    var usedDate: @RawValue LocalDateTime? = null,
    var reminderDate: @RawValue LocalDateTime?,
    var reminderOn: Boolean,
    var usedWhileOk : Boolean,
    var usedWhileFresh : Boolean,
    var usedNearExpiry : Boolean,
    var gotExpired : Boolean,
    var quantity : Double?,
    var measuringUnit : String?,
    var note : String?,
    var isFavourite : Boolean,
    var isArchived : Boolean,
    var isUsed : Boolean
) : Parcelable

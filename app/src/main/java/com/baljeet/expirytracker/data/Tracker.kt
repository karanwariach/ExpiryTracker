package com.baljeet.expirytracker.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "Tracker")
data class Tracker(
    @PrimaryKey(autoGenerate = true)
    var trackerId: Int,
    var productId: Int,
    var mfgDate: @RawValue LocalDateTime,
    var expiryDate: @RawValue LocalDateTime,
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

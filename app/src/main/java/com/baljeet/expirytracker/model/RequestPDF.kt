package com.baljeet.expirytracker.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.LocalDateTime

@Keep
@Parcelize
data class RequestPDF(
    var periodType : PeriodType = PeriodType.DAILY,
    var periodStartDate : @RawValue LocalDateTime?,
    var periodEndDate :@RawValue LocalDateTime?,
    var totalTracked : Int = 0,
    var totalExpired : Int = 0,
    var totalFresh : Int= 0,
    var totalNearExpiry : Int = 0,
    var totalOk : Int = 0,
    var resultCase : ResultCase = ResultCase.GOOD_JOB,
    var trackers : ArrayList<TrackerAndProduct> = ArrayList(),
    var groupBy : GroupBy = GroupBy.RESULTS,
    var useOfImages : UseImages = UseImages.ON,
    var textColor : SelectedTextColor = SelectedTextColor.BLACK
): Parcelable

enum class PeriodType{
    MONTHLY, WEEKLY,YEARLY,DAILY
}

enum class ResultCase{
    MORE_EXPIRED, GOOD_JOB, NEED_TO_IMPROVE, KEEP_IT_UP
}

enum class GroupBy {
    CATEGORIES, RESULTS
}

enum class SelectedTextColor{
    BLACK,GREY,BLUE
}

enum class UseImages{
    ON, OFF
}

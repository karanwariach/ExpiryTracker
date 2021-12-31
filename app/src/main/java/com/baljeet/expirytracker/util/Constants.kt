package com.baljeet.expirytracker.util

import kotlinx.datetime.TimeZone


object Constants {

    //Status
    const val PRODUCT_STATUS_ALL ="All"
    const val PRODUCT_STATUS_FRESH ="Fresh"
    const val PRODUCT_STATUS_EXPIRED ="Expired"
    const val PRODUCT_STATUS_EXPIRING ="Expiring"

    //Timezone
    val TIMEZONE = TimeZone.UTC

    //Favourite options
    const val SHOW_ALL = 1
    const val SHOW_ONLY_FAVOURITE = 2
    const val SHOW_ONLY_NON_FAVOURITE = 3

    //Periods types
    const val PERIOD_DAILY =1
    const val PERIOD_WEEKLY =2
    const val PERIOD_MONTHLY =3
    const val PERIOD_YEARLY =4

    //Charts for
    const val TOTAL_TRACKED =1
    const val USED_NEAR_EXPIRY =2
    const val USED_WHEN_FRESH = 3
    const val EXPIRED = 4

    //Donation amounts
    const val CANDY = 1.29
    const val CHOCOLATE = 2.50
    const val COFFEE = 6.88
    const val BURGER = 12.07
    const val MEAL = 29.99

    //Theme names
    const val GREEN  = "GREEN"
    const val BLUE  = "BLUE"
    const val PEACH  = "PEACH"
    const val YELLOW  = "YELLOW"
    const val BLACK  = "BLACK"
    const val WHITE  = "WHITE"
    const val PURPLE  = "PURPLE"
    const val PINK  = "PINK"
    const val TEAL  = "TEAL"
}
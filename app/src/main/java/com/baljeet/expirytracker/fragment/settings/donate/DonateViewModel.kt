package com.baljeet.expirytracker.fragment.settings.donate

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.util.Constants
import kotlinx.coroutines.launch

class DonateViewModel(app : Application): AndroidViewModel(app) {
    val context = getApplication<CustomApplication>()

    private var billingClient: BillingClient

    val skuLiveList = MutableLiveData<MutableList<SkuDetails>>()

    val purchaseComplete = MutableLiveData(false)

    init {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK ){
                purchases?.let {
                    for(purchase in it){
                        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged){
                            validatePurchase(purchase)
                        }
                    }
                }
            }else{
                purchaseComplete.postValue(false)
            }
        }
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        connectToGooglePlayBilling()
    }
    fun purchaseItem(activity : Activity, sku: SkuDetails){
        billingClient.launchBillingFlow(activity,
            BillingFlowParams.newBuilder().setSkuDetails(sku)
                .build() )
    }

    private fun validatePurchase(purchase : Purchase){
        val ackParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val listener = ConsumeResponseListener { billingResult, s ->
            Log.d("Log for - ","acknowledged")
            purchaseComplete.postValue(true)
        }
        billingClient.consumeAsync(ackParams,listener)
    }



    private fun connectToGooglePlayBilling(){
        viewModelScope.launch {
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingServiceDisconnected() {
                        connectToGooglePlayBilling()
                    }

                    override fun onBillingSetupFinished(result: BillingResult) {
                        if (result.responseCode == BillingClient.BillingResponseCode.OK){
                            getProductDetails()
                        }
                    }
                }
            )
        }
    }


    private fun getProductDetails(){
        val skuList = ArrayList<String>()
        skuList.clear()
        skuList.add(Constants.CANDY_ID)
        skuList.add(Constants.CHOCOLATE_ID)
        skuList.add(Constants.COFFEE_ID)
        skuList.add(Constants.BURGER_ID)
        skuList.add(Constants.DINNER_ID)
        val productsDetailQuery =  SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(
            productsDetailQuery
        ) { result, list ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                list?.let {
                    skuLiveList.postValue(it)
                }
            }
        }
    }


}
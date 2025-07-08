package net.broachcutter.vendorapp.screens.coupon.repo

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.*
import net.broachcutter.vendorapp.api.BroachCutterApi
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.util.Constants
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject

class CouponRepository @Inject constructor(
    private val broachCutterApi: BroachCutterApi,
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences
) {
    fun getAppliedCouponFromPref(): Coupon? {
        val couponListJSON: String? = sharedPreferences.getString(Constants.COUPON, null)
        return try {
            if (couponListJSON.isNullOrEmpty()) {
                return null
            }
            val type: Type = object : TypeToken<Coupon>() {}.type
            gson.fromJson(couponListJSON, type)
        } catch (exception: Exception) {
            Timber.e(exception)
            null
        }
    }

    fun getCouponListFromPref(): List<Coupon> {
        val couponListJSON: String = sharedPreferences.getString(Constants.COUPON_LIST, "") ?: ""
        return try {
            if (couponListJSON.isEmpty()) {
                return ArrayList()
            }
            val type: Type = object : TypeToken<List<Coupon>>() {}.type
            gson.fromJson(couponListJSON, type)
        } catch (exception: Exception) {
            Timber.e(exception)
            ArrayList()
        }
    }

    fun saveCouponToPref(coupon: Coupon) {
        try {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            val couponsJson: String = gson.toJson(coupon)
            editor.putString(Constants.COUPON, couponsJson)
            editor.apply()
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    fun saveCouponListToPref(couponList: List<Coupon>) {
        try {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            val couponsJson: String = gson.toJson(couponList)
            editor.putString(Constants.COUPON_LIST, couponsJson)
            editor.apply()
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    fun clearCouponFromPref() {
        try {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(Constants.COUPON, "")
            editor.apply()
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    suspend fun getAllCoupons(): LiveData<Resource<List<Coupon>>> {
        val allCouponList: MutableLiveData<Resource<List<Coupon>>> = MutableLiveData()
        allCouponList.postValue(Resource.loading())
        try {
            withContext(Dispatchers.IO) {
                val response = broachCutterApi.getAllCoupons()
                allCouponList.postValue(Resource.success(response.data))
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            val appException = AppException(ex)
            allCouponList.postValue(Resource.error(appException.message, null, appException))
        }
        return allCouponList
    }
}

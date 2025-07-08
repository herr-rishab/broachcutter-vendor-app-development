package net.broachcutter.vendorapp.util

import android.net.Uri
import java.net.URLDecoder

fun getFinalPriceUrl(
    baseUrl: String,
    uid: String,
    partNumber: String,
    paymentTermsId: String,
    quantity: String
): String {
    val builder = Uri.Builder()
    builder
        .authority(baseUrl)
        .appendPath("cart")
        .appendPath(uid)
        .appendPath("finalitemprice")
        .appendQueryParameter("partNumber", partNumber)
        .appendQueryParameter("paymentTermsId", paymentTermsId)
        .appendQueryParameter("quantity", quantity)
        .build()
    return URLDecoder.decode(builder.toString(), "UTF-8").drop(2)
}

fun getPaymentTermsUrl(baseUrl: String, uid: String): String {
    val builder = Uri.Builder()
    builder
        .authority(baseUrl)
        .appendPath("cart")
        .appendPath(uid)
        .appendPath("paymentTerms")
        .build()
    return URLDecoder.decode(builder.toString(), "UTF-8").drop(2)
}

fun submitOrdersUrl(baseUrl: String, uid: String): String {
    val builder = Uri.Builder()
    builder
        .authority(baseUrl)
        .appendPath("cart")
        .appendPath(uid)
        .appendPath("placeOrder")
        .build()
    return URLDecoder.decode(builder.toString(), "UTF-8").drop(2)
}

fun getOrderHistoryUrl(baseUrl: String, month: String, year: String): String {
    val builder = Uri.Builder()
    builder
        .authority(baseUrl)
        .appendPath("user")
        .appendPath("orderHistory")
        .appendQueryParameter("month", month)
        .appendQueryParameter("year", year)
        .build()
    return URLDecoder.decode(builder.toString(), "UTF-8").drop(2)
}

fun getRegisterUserUrl(baseUrl: String): String {
    val builder = Uri.Builder()
    builder
        .authority(baseUrl)
        .appendPath("user")
        .appendPath("register")
        .build()
    return URLDecoder.decode(builder.toString(), "UTF-8").drop(2)
}

fun getUserUrl(baseUrl: String): String {
    val builder = Uri.Builder()
    builder
        .authority(baseUrl)
        .appendPath("user")
        .build()
    return URLDecoder.decode(builder.toString(), "UTF-8").drop(2)
}

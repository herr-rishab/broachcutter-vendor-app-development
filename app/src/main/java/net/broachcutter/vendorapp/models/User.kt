package net.broachcutter.vendorapp.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("email")
    val email: String,
    @SerializedName("firebase_uid")
    val fid: String,
)

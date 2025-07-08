package net.broachcutter.vendorapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArborRequestModel(
    val morseTaper: String,
    val shankDiameter: Double? = null,
    val depthOfCut: Double? = null
) : Parcelable

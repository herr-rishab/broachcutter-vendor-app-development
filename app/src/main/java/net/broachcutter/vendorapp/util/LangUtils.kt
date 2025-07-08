package net.broachcutter.vendorapp.util

import android.os.Parcel
import android.os.Parcelable

/**
 * https://stackoverflow.com/a/56254187/3460025
 */
fun <T : Parcelable> deepCopy(obj: T): T? {
    var parcel: Parcel? = null
    return try {
        parcel = Parcel.obtain()
        parcel.writeParcelable(obj, 0)
        parcel.setDataPosition(0)
        parcel.readParcelable(obj::class.java.classLoader)
    } finally {
        parcel?.recycle()
    }
}

fun <T : Parcelable> deepCopyList(obj: List<T>): ArrayList<T> {
    val newList = ArrayList<T>()
    obj.forEach { listItem ->
        deepCopy(listItem)?.let { newList.add(it) }
    }
    return newList
}

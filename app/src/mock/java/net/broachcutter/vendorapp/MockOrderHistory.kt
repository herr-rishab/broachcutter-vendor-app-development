package net.broachcutter.vendorapp

import net.broachcutter.vendorapp.models.TrackingDetail
import net.broachcutter.vendorapp.models.UpdatedOrder
import net.broachcutter.vendorapp.models.UpdatedOrderItem
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

val updatedMockHistory = arrayListOf(
    UpdatedOrder(
        "1123891237812",
        ZonedDateTime.now(),
        49999.9,
        listOf(
            UpdatedOrderItem(null, 50000f, 3),
            UpdatedOrderItem(null, 70000f, 3)
        ),
        ZonedDateTime.now(),
        UpdatedOrderStatus.DISPATCHED,
        null,
        30000.00,
        null,
        ZonedDateTime.now(),
        TrackingDetail("FedEx", "1234567890"),
        docDueDate = ZonedDateTime.parse("06-08-2021")
    ),
    UpdatedOrder(
        "1123891237813",
        ZonedDateTime.now(),
        79999.9,
        listOf(
            UpdatedOrderItem(null, 90000f, 5),
            UpdatedOrderItem(null, 170000f, 2),
            UpdatedOrderItem(null, 80000f, 2)
        ),
        ZonedDateTime.now(),
        UpdatedOrderStatus.DISPATCHED,
        null,
        30000.00,
        null,
        ZonedDateTime.now(),
        TrackingDetail("FedEx", "1234567891"),
        docDueDate = ZonedDateTime.parse("06-08-2021")

    ),
    UpdatedOrder(
        "1123891237812",
        ZonedDateTime.now(),
        49999.9,
        listOf(
            UpdatedOrderItem(null, 50000f, 3),
            UpdatedOrderItem(null, 70000f, 3)
        ),
        ZonedDateTime.now(),
        UpdatedOrderStatus.DISPATCHED,
        null,
        30000.00,
        null,
        ZonedDateTime.now(),
        TrackingDetail("FedEx", "1234567892"),
        docDueDate = ZonedDateTime.parse("06-08-2021")

    ),
    UpdatedOrder(
        "1123891237813",
        ZonedDateTime.now(),
        79999.9,
        listOf(
            UpdatedOrderItem(null, 90000f, 5),
            UpdatedOrderItem(null, 170000f, 2),
            UpdatedOrderItem(null, 80000f, 2)
        ),
        ZonedDateTime.now(),
        UpdatedOrderStatus.DISPATCHED,
        null,
        30000.00,
        null,
        ZonedDateTime.now(),
        TrackingDetail("FedEx", "1234567893"),
        docDueDate = ZonedDateTime.parse("06-08-2021")

    ),
    UpdatedOrder(
        "1123891237812",
        ZonedDateTime.now(),
        49999.9,
        listOf(
            UpdatedOrderItem(null, 50000f, 3),
            UpdatedOrderItem(null, 70000f, 3)
        ),
        ZonedDateTime.now(),
        UpdatedOrderStatus.PENDING,
        null,
        30000.00,
        null,
        ZonedDateTime.now(),
        TrackingDetail("FedEx", "1234567894"),
        docDueDate = null

    ),
    UpdatedOrder(
        "1123891237813",
        ZonedDateTime.now(),
        79999.9,
        listOf(
            UpdatedOrderItem(null, 90000f, 5),
            UpdatedOrderItem(null, 170000f, 2),
            UpdatedOrderItem(null, 80000f, 2)
        ),
        null,
        UpdatedOrderStatus.PENDING,
        null,
        30000.00,
        null,
        ZonedDateTime.now(),
        TrackingDetail("FedEx", "1234567895"),
        docDueDate = null

    ),

)

fun stringToZoneDateTime(time: String): ZonedDateTime {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dt: LocalDate = LocalDate.parse(time, formatter)
    return dt.atStartOfDay(ZoneId.systemDefault())
}

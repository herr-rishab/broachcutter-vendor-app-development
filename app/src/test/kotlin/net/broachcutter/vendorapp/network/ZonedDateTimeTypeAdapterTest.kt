package net.broachcutter.vendorapp.network

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.Test
import org.threeten.bp.Month

class ZonedDateTimeTypeAdapterTest {

    private val zonedDateTimeTypeAdapter = ZonedDateTimeTypeAdapter()

    @Test
    fun deserialize() {
        val dateTime1 = "2018-09-01T16:21:57"
        val result1 = zonedDateTimeTypeAdapter.parseDateTime(dateTime1)
        result1.shouldNotBeNull()
        result1?.run {
            year shouldEqual 2018
            month shouldEqual Month.SEPTEMBER
            dayOfMonth shouldEqual 1
            hour shouldEqual 16
            minute shouldEqual 21
            second shouldEqual 57
            zone.id shouldEqual "Asia/Kolkata"
        }

        val invalidDateTime2 = ""
        val result3 = zonedDateTimeTypeAdapter.parseDateTime(invalidDateTime2)
        result3 shouldEqual null
    }

    @Test
    fun deserializeWithZone() {
        val dateTime1 = "2022-02-09T11:11:12.191Z"
        val result1 = zonedDateTimeTypeAdapter.parseDateTime(dateTime1)
        result1.shouldNotBeNull()
        result1?.run {
            year shouldEqual 2022
            month shouldEqual Month.FEBRUARY
            dayOfMonth shouldEqual 9
            hour shouldEqual 11
            minute shouldEqual 11
            second shouldEqual 12
            zone.id shouldEqual "Asia/Kolkata"
        }
    }

    @Test
    fun deserializeWithIST() {
        val dateTime1 = "2022-02-09T12:23:38.793+0530"
        val result1 = zonedDateTimeTypeAdapter.parseDateTime(dateTime1)
        result1.shouldNotBeNull()
        result1?.run {
            year shouldEqual 2022
            month shouldEqual Month.FEBRUARY
            dayOfMonth shouldEqual 9
            hour shouldEqual 12
            minute shouldEqual 23
            second shouldEqual 38
            zone.id shouldEqual "Asia/Kolkata"
        }
    }

    @Test
    fun deserializeWithOnlyDate() {
        val dateTime1 = "2022-02-09"
        val result1 = zonedDateTimeTypeAdapter.parseDateTime(dateTime1)
        result1.shouldNotBeNull()
        result1?.run {
            year shouldEqual 2022
            month shouldEqual Month.FEBRUARY
            dayOfMonth shouldEqual 9
            hour shouldEqual 0
            minute shouldEqual 0
            second shouldEqual 0
            zone.id shouldEqual "Asia/Kolkata"
        }
    }
}

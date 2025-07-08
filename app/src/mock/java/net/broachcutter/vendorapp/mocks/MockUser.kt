package net.broachcutter.vendorapp.mocks

import net.broachcutter.vendorapp.models.Credit
import net.broachcutter.vendorapp.models.UserDetail

val mockUser = UserDetail(
    userId = "1234567890",
    name = "Amit Tandon",
    addresses = listOf("XYZ building, 123 Main road, Somecity - 123456"),
    credit = Credit(40000f, 50000f),
    email = "amit.tandon@gmail.com",
    paymentOverdue = false,
    phoneNumber = "+919876543210"
)

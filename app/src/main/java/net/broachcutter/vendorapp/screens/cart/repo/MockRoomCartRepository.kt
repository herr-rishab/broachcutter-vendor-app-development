package net.broachcutter.vendorapp.screens.cart.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.*
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.models.cart.*
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse.FailedOrder.Items
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse.SuccessfulOrder.OrderItem
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import timber.log.Timber

@Suppress("MagicNumber", "TooManyFunctions")
object MockRoomCartRepository : BaseRoomCartRepository() {

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)
    }

    override suspend fun updatePaymentTerms(
        cartItem: CartItem,
        newTerms: PaymentTerm,
        newQuantity: Int
    ): CartItem {
        val paymentTerms = when {
            cartItem.product.partNumber == CUB_XL_PART_NUMBER -> {

                val termsResponse = withContext(Dispatchers.IO) {
                    val list = arrayListOf<ItemTerm>()
                    val termList = arrayListOf<PaymentTerm>()
                    list.add(ItemTerm("P0030", termList))
                    PaymentTermsResponse(list)
                }
                val firstTerm = termsResponse.itemTerms.get(0).terms.get(0)
                firstTerm
            }

            cartItem.selectedPaymentTerm != newTerms -> {
                // we need to apply the new payment terms
                newTerms
            }

            else -> return cartItem
        } // payment terms are the same as before

        Timber.i("Payment terms to be applied: ${paymentTerms.id}")
        // update to the determined payment terms
        cartItem.selectedPaymentTerm = paymentTerms

        val pricedCartItem = SimplePricedCartItem(
            cartItem.quantity,
            cartItem.partNumber,
            paymentTerms.id,
            Pricing(70.0, 5.0f, 60.0), ""
        )

        cartItem.unitPrice = pricedCartItem.pricing
        cartDao.updateCartItem(cartItem)
        return cartItem
    }

    override suspend fun fetchPaymentTerms(paymentTermsRequest: PaymentTermsRequest): PaymentTermsResponse {
        Timber.d("fetching payment terms")
        // simulate network call
        delay(500)

        val itemTerms = ArrayList<ItemTerm>()
        paymentTermsRequest.cartItems.forEach {
            val p00 =
                PaymentTerm("P0010", "Payment against PI", 10.0, 19)
            val p30 =
                PaymentTerm(
                    "P0030",
                    "Payment within 30 days",
                    60.0, 20
                )
            val terms = arrayListOf(p00, p30)
            val itemTerm = ItemTerm(it.partNumber, terms)
            itemTerms.add(itemTerm)
        }
        return PaymentTermsResponse(itemTerms)
    }

    override suspend fun getItemPricing(
        cartItem: CartItem,
        paymentTerm: PaymentTerm
    ): SimplePricedCartItem {
        Timber.d("getting item pricing")
        delay(500)
        val pricing = when (cartItem.partNumber) {
            "200112" -> Pricing(
                10000.0,
                10f,
                9000.0
            ) // from cutters search
            else -> {
                val basePrice = 5000.0 * cartItem.quantity
                val discPercent = paymentTerm.discountPercent
                val finalPrice = basePrice * (100 - discPercent) / 100
                Pricing(
                    basePrice,
                    discPercent.toFloat(),
                    finalPrice
                )
            }
        }
        return SimplePricedCartItem(
            cartItem.quantity,
            cartItem.partNumber,
            paymentTerm.id,
            pricing,
            ""
        )
    }

    @Suppress("LongMethod")
    override fun submitOrder(
        cartUiModel: CartUiModel,
        appliedCoupon: String?
    ): LiveData<Resource<PlaceOrderResponse>> {
        val response: MutableLiveData<Resource<PlaceOrderResponse>> = MutableLiveData()
        response.postValue(Resource.loading(null))
        GlobalScope.launch {
            delay(1000)

            val orderItemList = arrayListOf<OrderItem>()
            orderItemList.add(
                OrderItem(
                    OrderItem
                        .Item(
                            "BROACHCUTTER®,  Model:  SUPER Magnetic Base Drilling Machine",
                            "90001"
                        ),
                    4000.0, 4
                )
            )
            orderItemList.add(
                OrderItem(
                    OrderItem
                        .Item(
                            "BROACHCUTTER®,  Model:  SUPER Magnetic Base Welding Machine",
                            "90002"
                        ),
                    4000.0, 4
                )
            )

            val taxesList = arrayListOf<PlaceOrderResponse.SuccessfulOrder.Taxes>()
            taxesList.add(PlaceOrderResponse.SuccessfulOrder.Taxes(5000.0, 9.0, "IGST"))

            val completeOrder1 = PlaceOrderResponse.SuccessfulOrder(
                orderDate = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                orderItems = orderItemList,
                orderNumber = "4925",
                status = "Confirmed",
                subtotal = 4500.0,
                taxes = taxesList,
                total = 47430.0
            )

            val completeOrder2 = PlaceOrderResponse.SuccessfulOrder(
                orderDate = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                orderItems = orderItemList,
                orderNumber = "4928",
                status = "Confirmed",
                subtotal = 4500.0,
                taxes = taxesList,
                total = 47430.0
            )

            val pendingOrder = PlaceOrderResponse.SuccessfulOrder(
                orderDate = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                orderItems = orderItemList,
                orderNumber = "4926",
                status = "Pending",
                subtotal = 4500.0,
                taxes = taxesList,
                total = 47430.0
            )

            val itemList = arrayListOf<Items>()
            itemList.add(
                Items(
                    SimplePricedCartItem(
                        cartQuantity = 4,
                        partNumber = "90001",
                        paymentTermId = "P0070",
                        pricing = Pricing(
                            basePrice = 70.0,
                            discountPercent = 15000f,
                            finalPrice = 50000.0
                        ),
                        name = "BROACHCUTTER®,  Model:  SUPER Magnetic Base Drilling Machine"
                    )
                )
            )
            val failedOrder = PlaceOrderResponse.FailedOrder(
                items = itemList,
                "Couldn't connect to HANA"
            )

            val successFulList = arrayListOf<PlaceOrderResponse.SuccessfulOrder>()
            successFulList.add(completeOrder1)
            successFulList.add(completeOrder2)
            successFulList.add(pendingOrder)

            response.postValue(Resource.success(PlaceOrderResponse(failedOrder, successFulList)))
        }
        return response
    }

    override suspend fun fetchFinalCartPrice(totalCart: TotalCart): CartPrice {
        return CartPrice(
            itemSubTotal = 348.59,
            totalDiscountPrice = 1006.6,
            couponDiscount = 82.8, totalTax = 62.74,
            totalPrice = 411.33
        )
    }
}

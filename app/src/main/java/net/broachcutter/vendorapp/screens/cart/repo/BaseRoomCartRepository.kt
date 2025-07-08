package net.broachcutter.vendorapp.screens.cart.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.valartech.commons.network.google.Resource
import com.valartech.commons.network.google.Status
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.db.CartDao
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.cart.*
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.screens.cart.areReadyCartItems
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import net.broachcutter.vendorapp.util.Constants.MIN_CART_TOTAL_RUPEES
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.max

@Suppress("TooManyFunctions")
abstract class BaseRoomCartRepository : CartRepository {

    @set:Inject
    lateinit var cartDao: CartDao

    @set:Inject
    lateinit var userRepository: UserRepository

    @set:Inject
    lateinit var analytics: Analytics

    private val addUpdateResult = MutableLiveData<Resource<Any>>()

    private val cartLiveData: MutableLiveData<Resource<Cart>> = MutableLiveData()

    private val cartEmptyLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var lastCartValue: Cart? = null

    companion object {
        const val GST_TAX_ITEM = "GST"
        const val CUB_PART_NUMBER = "900001"
        const val CUB_XL_PART_NUMBER = "900015"
    }

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)
        cartLiveData.observeForever { resource ->
            if (resource?.status == Status.SUCCESS) {
                // cart has been updated, fetch payment terms if needed
                resource.data?.let { cart ->
                    // check to avoid recursive loop
                    if (lastCartValue != cart) {
                        if (cart.isEmptyCart()) {
                            val emptyCart = Cart.createEmptyCart(null)
                            cartLiveData.postValue(
                                Resource.success(emptyCart)
                            )
                            lastCartValue = emptyCart
                        } else {
                            updateCart(resource, cart) { lastCartValue = it }
                        }
                    }
                }
            }
        }
        triggerCartEmptyCheck()
    }

    /**
     * Called on init so that we can immediately fetch a value for the [cartEmptyLiveData].
     */
    @DelicateCoroutinesApi
    private fun triggerCartEmptyCheck() {
        Timber.i("triggerCartEmptyCheck")
        GlobalScope.launch {
            val cartItem = withContext(IO) { cartDao.getCartItems() }
            if (cartItem.isEmpty()) {
                cartEmptyLiveData.postValue(true)
            } else {
                cartEmptyLiveData.postValue(false)
            }
        }
    }

    private suspend fun triggerCartEmptyCheckAsync() {
        Timber.i("triggerCartEmptyCheckAsync")
        val cartItem = withContext(IO) { cartDao.getCartItems() }
        if (cartItem.isEmpty()) {
            cartEmptyLiveData.postValue(true)
        } else {
            /**
             * when Add to cart from the coupon is added
             * the value in the `cartEmptyLiveData` is set
             */
            cartEmptyLiveData.postValue(false)
        }
    }

    /**
     * Fetches payment terms and merges them into the cart.
     */
    @DelicateCoroutinesApi
    private fun updateCart(resource: Resource<Cart>, cart: Cart, updatedCart: (Cart) -> Unit) {
        cartLiveData.postValue(Resource.loading(resource.data))
        val paymentTermsRequest = PaymentTermsRequest(cart)
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val paymentTerms = withContext(IO) {
                    fetchPaymentTerms(paymentTermsRequest)
                }
                mergePaymentTerms(cart, paymentTerms)
                if (lastCartValue?.isReadyCart() == false && cart.isReadyCart()) {
                    analytics.checkoutAllPaymentTermsSelected()
                }
                updatedCart.invoke(cart)
                cartLiveData.postValue(Resource.success(cart))
            } catch (ex: Exception) {
                Timber.e(ex)
                val appException = AppException(ex)
                cartLiveData.postValue(Resource.error(appException.message, null, appException))
            }
        }
    }

    private fun mergePaymentTerms(cart: Cart, paymentTerms: PaymentTermsResponse) {
        cart.cartItems?.forEach { cartItem ->
            paymentTerms.itemTerms.forEach {
                if (cartItem.partNumber == it.partNumber) {
                    cartItem.paymentTerms = it.terms
                }
            }
        }
    }

    abstract suspend fun fetchPaymentTerms(paymentTermsRequest: PaymentTermsRequest): PaymentTermsResponse

    override fun getCart(): LiveData<Resource<Cart>> {
        GlobalScope.launch {
            try {
                cartLiveData.postValue(Resource.loading())
                val cartItems = withContext(IO) {
                    cartDao.getCartItems()
                }
                if (cartItems.areReadyCartItems()) {
                    // fetch tax and totals
                    val pricedCart = fetchTaxAndTotals(cartItems)
                    // check credit and overdue payments
                    val completeCart = checkCartConstraints(pricedCart)

                    cartLiveData.postValue(Resource.success(completeCart))
                } else {
                    cartLiveData.postValue(
                        Resource.success(
                            Cart(
                                cartItems,
                                null,
                                null,
                                null,
                                minRupeeAmountRequired = 0.0,
                                deliveryAddress = getDeliveryAddress()
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                val appException = AppException(ex)
                cartLiveData.postValue(Resource.error(appException.message, null, appException))
            }
        }

        return cartLiveData
    }

    /**
     * Checks if the user:
     * 1. Has enough credit available
     * 2. Has any payments overdue
     * 3. Has annular cutters worth more than [MIN_CART_TOTAL_RUPEES], if at all
     */
    private suspend fun checkCartConstraints(cart: Cart): Cart {
        val userDetail = userRepository.getUserDetail()
        val availableCredit = userDetail.credit.availableCredit

        val nonCreditTotal = getTotalPriceOfNonCreditItem(cart)
        if (cart.total != null) {
            // note that taxes on credit items are also counted against credit limit
            val creditTotal = cart.total - nonCreditTotal
            cart.exceededCredit = if (creditTotal > availableCredit) {
                creditTotal - availableCredit
            } else 0.0
        }
        cart.overduePayment = userDetail.paymentOverdue ?: false
        return checkMinCutterConstraint(cart)
    }

    /**
     * Includes taxes.
     */
    private fun getTotalPriceOfNonCreditItem(cart: Cart): Double {
        var total = 0.0
        /**
         * In this we are calculating total of non-credit items
         * Note : paymentTerms started with P00 is non-credit items
         */
        cart.cartItems?.let { cartItems ->
            cartItems.forEach { cartItem ->
                /**
                 * Checking item is non-credit item
                 */
                val isNonCreditItem = cartItem.selectedPaymentTerm?.id?.startsWith("P00")
                    ?: false

                if (isNonCreditItem) {
                    cartItem.getLineItemPrice()?.let { pricing ->
                        // add up price to make subtotal
                        total += pricing.finalPrice
                        // calculate and add tax
                        val cartItemTax = pricing.finalPrice * cartItem.product.taxRate / 100
                        total += cartItemTax
                    }
                }
            }
        }
        return total
    }

    /**
     * Sets [Cart.minRupeeAmountRequired] to the appropriate value for this cart
     */
    private fun checkMinCutterConstraint(cart: Cart): Cart {
        /**
         * If we have only spares in our cart, there is no min amt constraint.
         *
         * If we have anything other than a spare present in the cart, min [MIN_CART_TOTAL_RUPEES]
         * is needed.
         */

        return if (cart.minBalanceCheckNeeded()) {

            // Minimum cutter cart constraint should be included the tax amount
            val cartTotal = cart.total ?: 0.0

            // gives 0 in case the first value is negative, i.e., we have already crossed the min amount
            cart.minRupeeAmountRequired = max((MIN_CART_TOTAL_RUPEES - cartTotal), 0.0)
            cart
        } else {
            cart.minRupeeAmountRequired = 0.0
            cart
        }
    }

    @DelicateCoroutinesApi
    override fun addToOrUpdateCart(product: Product, quantity: Int): LiveData<Resource<Any>> {
        addUpdateResult.value = Resource.loading()
        GlobalScope.launch(IO) {
            val currentCartItem =
                withContext(Dispatchers.Default) {
                    cartDao.getCartItem(
                        product.partNumber
                    )
                }
            if (currentCartItem == null || currentCartItem.quantity == 0) {
                // new item added to cart
                val cartItem = CartItem(
                    product,
                    product.partNumber,
                    quantity,
                    null,
                    null,
                    null
                )
                val res = async { cartDao.addToCart(cartItem) }
                addUpdateResult.postValue(Resource.success(res.await()))
                analytics.addToCart(product, quantity)
            } else {
                // old item's quantity updated
                val oldQty = currentCartItem.quantity
                currentCartItem.quantity += quantity
                val res = async { cartDao.updateCartItem(currentCartItem) }
                addUpdateResult.postValue(Resource.success(res.await()))
                analytics.updateCartQuantity(product, oldQty, currentCartItem.quantity)
            }
            triggerCartEmptyCheckAsync()
        }

        return addUpdateResult
    }

    /**
     * Fetches or gets the cached delivery address
     */
    protected suspend fun getDeliveryAddress(): String {
        return userRepository.getUserDetail().addresses?.get(0) ?: ""
    }

    @DelicateCoroutinesApi
    override fun removeFromCart(product: Product) {
        Timber.i("removeFromCart")
        cartLiveData.value = Resource.loading()
        GlobalScope.launch(IO) {
            val currentCartItem = cartDao.getCartItem(product.partNumber)
            if (currentCartItem != null) {
                // delete item from cart
                cartDao.deleteFromCart(currentCartItem)
                analytics.removeFromCart(currentCartItem)
                getCart()
            }
            triggerCartEmptyCheckAsync()
        }
    }

    @DelicateCoroutinesApi
    override fun onCartItemUpdate(cartItem: CartItem, newQuantity: Int, newTerms: PaymentTerm) {
        if (newQuantity == 0) {
            // just remove the item from cart, we don't need to bother with payment terms
            return removeFromCart(cartItem.product)
        }
        cartLiveData.value = Resource.loading()
        GlobalScope.launch(IO) {
            try {
                val currentCartItem = cartDao.getCartItem(cartItem.partNumber)
                if (currentCartItem != null) {
                    val oldQuantity = currentCartItem.quantity
                    val updatedCartItem = updatePaymentTerms(currentCartItem, newTerms, newQuantity)
                    Timber.i("updatedCartItem has payment terms: ${updatedCartItem.selectedPaymentTerm?.id}")
                    updateCartItemQty(updatedCartItem, newQuantity)
                    if (oldQuantity != newQuantity) {
                        analytics.updateCartQuantity(cartItem.product, oldQuantity, newQuantity)
                    }
                    getCart()
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                val appException = AppException(ex)
                cartLiveData.postValue(Resource.error(appException.message, null, appException))
            }
        }
    }

    protected open suspend fun updatePaymentTerms(
        cartItem: CartItem,
        newTerms: PaymentTerm,
        newQuantity: Int
    ): CartItem {
        if (cartItem.selectedPaymentTerm != newTerms) {
            cartItem.selectedPaymentTerm = newTerms
            val pricedCartItem = getItemPricing(
                cartItem,
                newTerms
            )
            cartItem.unitPrice = pricedCartItem.pricing
            cartDao.updateCartItem(cartItem)
        }
        return cartItem
    }

    private suspend fun updateCartItemQty(cartItem: CartItem, newQuantity: Int) {
        if (cartItem.quantity != newQuantity) {
            cartItem.quantity = newQuantity
            cartItem.selectedPaymentTerm?.let {
                // compute new pricing
                val pricing = getItemPricing(cartItem, it).pricing
                cartItem.unitPrice = pricing
            }
            // save to db
            cartDao.updateCartItem(cartItem)
        }
    }

    private suspend fun fetchTaxAndTotals(cartItems: List<CartItem>): Cart {
        var subtotal = 0.0
        var taxTotal = 0.0
        val taxLineItemsMap = HashMap<Float, TaxItem>()

        cartItems.forEach { cartItem ->
            cartItem.getLineItemPrice()?.let { pricing ->
                val taxRate = cartItem.product.taxRate
                // add up price to make subtotal
                subtotal += pricing.finalPrice
                // get the relevant tax line item
                val mapItem = taxLineItemsMap[taxRate]
                val taxItem = if (mapItem != null) {
                    mapItem
                } else {
                    // create a new line item if one doesn't exist in the map
                    val newTaxItem = TaxItem(GST_TAX_ITEM, taxRate)
                    taxLineItemsMap[taxRate] = newTaxItem
                    newTaxItem
                }

                // add the tax from this cart item to the tax line item
                val cartItemTax = pricing.finalPrice * taxRate / 100
                val updatedLineItemTax = taxItem.amount + cartItemTax
                taxItem.amount = updatedLineItemTax

                // add up taxes
                taxTotal += cartItemTax
            }
        }
        val total = subtotal + taxTotal

        return Cart(
            cartItems,
            ArrayList(taxLineItemsMap.values),
            subtotal,
            total,
            minRupeeAmountRequired = 0.0,
            deliveryAddress = getDeliveryAddress()
        )
    }

    /**
     * But the value is not been updated over here.
     * This issue is only seen when the coupon is applied and the product is added
     * in other condition its working fine
     */
    override fun isCartEmpty() = cartEmptyLiveData

    abstract suspend fun getItemPricing(
        cartItem: CartItem,
        paymentTerm: PaymentTerm
    ): SimplePricedCartItem

    @DelicateCoroutinesApi
    override fun clearCart() {
        GlobalScope.launch(IO) {
            cartDao.clearCart()
            triggerCartEmptyCheckAsync()
        }
    }
}

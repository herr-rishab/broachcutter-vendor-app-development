package net.broachcutter.vendorapp.screens.product_list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valartech.commons.utils.extensions.inflate
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.views.ProductCardView

class ProductListAdapter(private val productInteractor: ProductInteractor) :
    RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    var productList = listOf<Product>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(parent)

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productCardView = holder.itemView as ProductCardView
        val product = productList[position]
        productCardView.bind(product)
        // TODO check if this made any difference
//        productCardView.number_counter.setText("${productCardView.selectedQuantity}")
        productCardView.addToCartButton.setOnClickListener {
            productInteractor.addToCart(product, productCardView.selectedQuantity)
        }
    }

    class ProductViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.list_item_machine_product_list))

    interface ProductInteractor {
        fun addToCart(product: Product, quantity: Int)
    }
}

package net.broachcutter.vendorapp.models

import android.content.Context
import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.AccessoriesModelWiseListItemBinding

class AccessoriesModelWiseItem(
    private val context: Context,
    val product: Product,
    private val listener: ClickListener
) : BindableItem<AccessoriesModelWiseListItemBinding>() {

    interface ClickListener {
        fun onAddSingleQuantityToCart(product: Product)
    }

    override fun getLayout() = R.layout.accessories_model_wise_list_item
    override fun initializeViewBinding(view: View): AccessoriesModelWiseListItemBinding {
        return AccessoriesModelWiseListItemBinding.bind(view)
    }

    override fun bind(viewHolder: AccessoriesModelWiseListItemBinding, position: Int) {
        viewHolder.run {
            accessoriesModelWiseTitle.text = product.name
            accessoriesModelWisePartNumber.text =
                context.getString(R.string.item_number_s, product.partNumber)
            accessoriesModelWiseAddToCart.setOnClickListener {
                listener.onAddSingleQuantityToCart(
                    product
                )
            }
        }
    }
}

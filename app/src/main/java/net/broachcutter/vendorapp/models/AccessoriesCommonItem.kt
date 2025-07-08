package net.broachcutter.vendorapp.models

import android.content.Context
import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.AccessoriesCommonListItemBinding
import net.broachcutter.vendorapp.util.setSafeOnClickListener

class AccessoriesCommonItem(
    private val context: Context,
    val product: Product,
    val addToCart: (Product) -> Unit
) : BindableItem<AccessoriesCommonListItemBinding>() {
    override fun getLayout() = R.layout.accessories_common_list_item

    override fun initializeViewBinding(view: View): AccessoriesCommonListItemBinding {
        return AccessoriesCommonListItemBinding.bind(view)
    }

    override fun bind(viewHolder: AccessoriesCommonListItemBinding, position: Int) {
        viewHolder.accessoriesCommonTitle.text = product.name
        viewHolder.accessoriesCommonPartNumber.text =
            context.getString(R.string.item_number_s, product.partNumber)
        viewHolder.accessoriesCommonAddToCard.setSafeOnClickListener { addToCart.invoke(product) }
    }
}

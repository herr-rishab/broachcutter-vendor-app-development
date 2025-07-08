package net.broachcutter.vendorapp.screens.search

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.SearchMoreResultsItemBinding
import net.broachcutter.vendorapp.databinding.SearchResultItemBinding
import net.broachcutter.vendorapp.databinding.SearchSectionHeaderItemBinding
import net.broachcutter.vendorapp.databinding.SearchTitleItemBinding
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType

class TitleItem : BindableItem<SearchTitleItemBinding>() {
    override fun bind(viewBinding: SearchTitleItemBinding, position: Int) {
        // nothing to bind
    }

    override fun getLayout() = R.layout.search_title_item

    override fun initializeViewBinding(view: View) = SearchTitleItemBinding.bind(view)
}

class SectionHeaderItem(private val context: Context, private val productType: ProductType) :
    BindableItem<SearchSectionHeaderItemBinding>() {
    override fun bind(viewBinding: SearchSectionHeaderItemBinding, position: Int) {
        viewBinding.sectionText.setText(productType.nameRes)
        viewBinding.sectionText.setBackgroundColor(
            ContextCompat.getColor(
                context,
                productType.colorRes
            )
        )
    }

    override fun getLayout() = R.layout.search_section_header_item

    override fun initializeViewBinding(view: View) = SearchSectionHeaderItemBinding.bind(view)
}

class ResultItem(private val product: Product, private val clickListener: ClickListener) :
    BindableItem<SearchResultItemBinding>() {
    override fun bind(viewBinding: SearchResultItemBinding, position: Int) {
        viewBinding.resultText.text = product.name
        viewBinding.resultText.isSelected = true
        viewBinding.root.setOnClickListener {
            clickListener.onProductClick(product)
        }
    }

    override fun getLayout() = R.layout.search_result_item

    override fun initializeViewBinding(view: View) = SearchResultItemBinding.bind(view)

    interface ClickListener {
        fun onProductClick(product: Product)
    }
}

class MoreResultsItem(
    private val context: Context,
    private val productType: ProductType,
    private val clickListener: ClickListener
) : BindableItem<SearchMoreResultsItemBinding>() {
    override fun bind(viewBinding: SearchMoreResultsItemBinding, position: Int) {
        val name = context.getString(productType.nameRes)
        viewBinding.moreResultsText.text = context.getString(R.string.show_all_results, name)
        viewBinding.root.setOnClickListener {
            clickListener.onShowMoreResults(productType)
        }
    }

    override fun getLayout() = R.layout.search_more_results_item

    override fun initializeViewBinding(view: View) = SearchMoreResultsItemBinding.bind(view)

    interface ClickListener {
        fun onShowMoreResults(productType: ProductType)
    }
}

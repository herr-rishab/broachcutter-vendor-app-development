package net.broachcutter.vendorapp.screens.solid_drills.home

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class AutoCompleteTextViewAdapter(context: Context, layout: Int, list: List<String>) :
    ArrayAdapter<String>(context, layout, list) {

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                return null
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            }
        }
    }
}

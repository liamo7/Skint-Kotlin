package com.loh.skint.ui.record.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.loh.skint.R
import com.loh.skint.domain.model.Record
import com.loh.skint.injection.scope.FragmentScoped
import com.loh.skint.util.LONG_DATE_FORMAT
import com.loh.skint.util.colorize
import com.loh.skint.util.inflate
import com.loh.skint.util.tint
import kotlinx.android.synthetic.main.item_record.view.*
import javax.inject.Inject

@FragmentScoped
class RecordListAdapter @Inject constructor() : RecyclerView.Adapter<RecordListAdapter.ViewHolder>() {

    var callback: (Record) -> Unit = {}
    var records = listOf<Record>()
        set(value) {
            field = value
            notifyItemRangeChanged(0, field.size)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_record))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]

        holder.itemView.apply {
            setOnClickListener { callback(record) }
            item_record_name.text = context.getString(record.category.nameRes)
            item_record_amount.text = record.prettyAmount()
            item_record_amount.colorize(record.colorizeAmount())
            item_record_date.text = record.date.format(LONG_DATE_FORMAT)
            item_record_icon.setImageResource(record.category.iconRes)
            item_record_icon.tint(record.category.colorRes)
        }
    }

    override fun getItemCount() = records.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}


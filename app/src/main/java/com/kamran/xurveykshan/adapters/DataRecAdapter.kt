package com.kamran.xurveykshan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kamran.xurveykshan.R
import com.kamran.xurveykshan.databinding.SingleRowDataBinding
import com.kamran.xurveykshan.data.DataEntity

class DataRecAdapter : ListAdapter<DataEntity, DataRecAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(val binding: SingleRowDataBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleRowDataBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item.id == 0){
            holder.binding.column1.text = HtmlCompat.fromHtml("<b>Q1</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            holder.binding.column2.text = HtmlCompat.fromHtml("<b>Q2</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            holder.binding.column3.text = HtmlCompat.fromHtml("<b>Recording</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            holder.binding.column4.text = HtmlCompat.fromHtml("<b>Submit Time</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        else{
            holder.binding.column1.text = item.age.toString()
            holder.binding.column2.text = item.imageUri
            holder.binding.column3.text = item.recording
            holder.binding.column4.text = item.submitTime
        }

    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<DataEntity>(){
            override fun areItemsTheSame(
                oldItem: DataEntity,
                newItem: DataEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DataEntity,
                newItem: DataEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
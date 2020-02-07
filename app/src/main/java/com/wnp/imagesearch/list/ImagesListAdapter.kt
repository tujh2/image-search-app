package com.wnp.imagesearch.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wnp.imagesearch.R

class ImagesListAdapter : RecyclerView.Adapter<ImagesListViewHolder>() {
    private val imageList = mutableListOf<Image>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)

        val holder = ImagesListViewHolder(view)
        holder.itemView.setOnClickListener {

        }
        return holder
    }

    override fun onBindViewHolder(holder: ImagesListViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    fun setData(list: MutableList<Image>) {
        this.imageList.clear()
        this.imageList.addAll(list)
        notifyDataSetChanged()
    }
}
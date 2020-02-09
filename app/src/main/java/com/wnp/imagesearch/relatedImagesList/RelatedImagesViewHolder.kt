package com.wnp.imagesearch.relatedImagesList

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.wnp.imagesearch.GlideApp
import com.wnp.imagesearch.R

class RelatedImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.related_image_item)

    fun bind(imgUrl: String) {
        GlideApp.with(itemView.context)
            .load(imgUrl)
            .fitCenter()
            .into(imageView)
    }
}
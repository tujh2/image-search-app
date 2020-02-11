package com.wnp.imagesearch.relatedImagesList

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.wnp.imagesearch.GlideApp
import com.wnp.imagesearch.R


class RelatedImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.related_image_item)

    companion object {
        private const val TAG = "RelatedImagesViewHolder"
    }

    fun bind(image: RelatedImage) {
        GlideApp.with(itemView.context)
            .asBitmap()
            .load(image.siteUrl)
            .override(image.width, image.height)
            .into(imageView)
    }
}
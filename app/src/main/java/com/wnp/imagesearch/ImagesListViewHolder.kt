package com.wnp.imagesearch

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

class ImagesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = "Image View Holder"
    private val imageView: ImageView = itemView.findViewById(R.id.seatch_item_image)
    private val siteUrl: TextView = itemView.findViewById(R.id.search_item_link)
    private val descr: TextView = itemView.findViewById(R.id.search_item_description)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.search_item_progress)

    fun bind(image: Image) {
        //Log.d(TAG, image.imageURL)
        this.siteUrl.text = image.siteUrl
        this.descr.text = image.description

        itemView.post {
            imageView.layoutParams.height = itemView.width * image.height / image.width
            GlideApp.with(itemView.context)
                .load(image.imageUrl)
                .thumbnail(Glide.with(imageView.context).load(image.thumbnailUrl))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(TAG, "Error loading image")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .override(itemView.width, itemView.width * image.height / image.width)
                .into(imageView)
        }
    }

}
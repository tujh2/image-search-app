package com.wnp.imagesearch.imagesList

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wnp.imagesearch.GlideApp
import com.wnp.imagesearch.R
import com.wnp.imagesearch.relatedImagesList.RelatedImagesListAdapter

class ImagesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = "Image View Holder"
    private val imageView: ImageView = itemView.findViewById(R.id.search_item_image)
    private val siteUrl: TextView = itemView.findViewById(R.id.search_item_link)
    private val descr: TextView = itemView.findViewById(R.id.search_item_description)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.search_item_progress)
    private val relatedRecyclerView: RecyclerView = itemView.findViewById(R.id.related_images_list)
    private val relatedImagesLabel: TextView = itemView.findViewById(R.id.related_image_label)
    private var isRelatedOpened = false

    fun bind(image: Image) {
        //Log.d(TAG, image.imageURL)
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
            this.siteUrl.text = image.siteUrl
            this.descr.text = image.description
        }

    }

    fun openRelated(siteUrl: String) {
        if(isRelatedOpened) {
            relatedImagesLabel.visibility = View.GONE
            relatedRecyclerView.visibility = View.GONE
            relatedRecyclerView.adapter = null
            relatedRecyclerView.layoutManager = null
            isRelatedOpened = false
        } else {
            val relatedImagesAdapter = RelatedImagesListAdapter()
            relatedImagesAdapter.getRelatedImages(siteUrl)
            relatedImagesLabel.visibility = View.VISIBLE
            relatedRecyclerView.visibility = View.VISIBLE
            relatedRecyclerView.adapter = relatedImagesAdapter
            relatedRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            isRelatedOpened = true
        }
    }
}
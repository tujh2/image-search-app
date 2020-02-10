package com.wnp.imagesearch.imagesList

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wnp.imagesearch.GlideApp
import com.wnp.imagesearch.R
import com.wnp.imagesearch.relatedImagesList.RelatedImagesListAdapter

class ImagesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = "Image View Holder"
    private val imageView: ImageView = itemView.findViewById(R.id.search_item_image)
    private val siteUrl: TextView = itemView.findViewById(R.id.search_item_link)
    private val descr: TextView = itemView.findViewById(R.id.search_item_description)
    private val relatedProgressBar: ProgressBar =
        itemView.findViewById(R.id.related_image_progressbar)
    private var relatedRecyclerView: RecyclerView = itemView.findViewById(R.id.related_images_list)
    private val relatedImagesView: LinearLayout = itemView.findViewById(R.id.related_view)


    fun bind(image: Image) {
        relatedRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            adapter = image.relatedImagesListAdapter
        }
        //Log.d(TAG, image.imageURL)
        itemView.post {
            //itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            imageView.layoutParams.height = itemView.width * image.height / image.width
            GlideApp.with(itemView.context)
                .load(image.imageUrl)
                .thumbnail(Glide.with(imageView.context).load(image.thumbnailUrl))
                .into(imageView)
            this.siteUrl.text = image.siteUrl
            this.descr.text = image.description
            setRelatedVisibility(image.isRelatedOpened)
        }
    }

    private fun setRelatedVisibility(visible: Boolean) {
        if (visible) {
            relatedImagesView.visibility = View.VISIBLE
        } else {
            relatedImagesView.visibility = View.GONE
        }
    }

    fun openRelated(image: Image) {
        if (!image.isRelatedOpened) {
            relatedRecyclerView.adapter = null
            val relatedImagesAdapter = RelatedImagesListAdapter()
            relatedProgressBar.visibility = View.VISIBLE
            relatedRecyclerView.adapter = relatedImagesAdapter
            relatedImagesAdapter.loadRelatedImages(image.siteUrl)
                .observe(itemView.context as LifecycleOwner,
                    Observer<RelatedImagesListAdapter.Progress> {
                        when (it) {
                            RelatedImagesListAdapter.Progress.SUCCESS -> {
                                Log.d(TAG, "SUCCESS")
                                relatedProgressBar.visibility = View.GONE
                                image.relatedImagesListAdapter = relatedImagesAdapter
                            }
                            RelatedImagesListAdapter.Progress.IN_PROGRESS -> {
                                setRelatedVisibility(true)
                                Log.d(TAG, "IN_PROGRESS")
                                relatedProgressBar.visibility = View.VISIBLE
                                image.isRelatedOpened = true
                            }
                            RelatedImagesListAdapter.Progress.FAILED -> {
                                relatedProgressBar.visibility = View.GONE
                                Log.d(TAG, "FAILED")
                                image.isRelatedOpened = false
                            }
                        }
                    })
        }
    }
}
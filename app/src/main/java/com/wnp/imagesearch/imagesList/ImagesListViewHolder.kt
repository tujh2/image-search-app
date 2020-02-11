package com.wnp.imagesearch.imagesList

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
    private val imageView: ImageView = itemView.findViewById(R.id.search_item_image)
    private val siteUrl: TextView = itemView.findViewById(R.id.search_item_link)
    private val descr: TextView = itemView.findViewById(R.id.search_item_description)
    private val relatedProgressBar: ProgressBar =
        itemView.findViewById(R.id.related_image_progressbar)
    private val noRelatedFouundLabel: TextView = itemView.findViewById(R.id.no_related_label)
    private var relatedRecyclerView: RecyclerView = itemView.findViewById(R.id.related_images_list)
    private val relatedImagesView: LinearLayout = itemView.findViewById(R.id.related_view)


    fun bind(image: Image, screenWidth: Int) {
        relatedRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            adapter = image.relatedImagesListAdapter
        }
        setRelatedVisibility(image.isRelatedOpened)
        imageView.layoutParams.height = screenWidth * image.height / image.width
        GlideApp.with(itemView.context)
            .load(image.imageUrl)
            .thumbnail(Glide.with(imageView.context).load(image.thumbnailUrl))
            .into(imageView)
        this.siteUrl.text = image.siteUrl
        this.descr.text = image.description
    }

    private fun setRelatedVisibility(visible: Boolean) {
        if (visible) {
            relatedImagesView.visibility = View.VISIBLE
        } else {
            relatedImagesView.visibility = View.GONE
        }
    }

    fun openRelated(image: Image) {
        if (image.isRelatedOpened) {
            image.isRelatedOpened = false
            setRelatedVisibility(false)
        } else {
            relatedRecyclerView.adapter = null
            val relatedImagesAdapter = RelatedImagesListAdapter()
            relatedProgressBar.visibility = View.VISIBLE
            relatedRecyclerView.adapter = relatedImagesAdapter
            relatedImagesAdapter.loadRelatedImages(image.siteUrl)
                .observe(itemView.context as LifecycleOwner,
                    Observer<RelatedImagesListAdapter.Progress> {
                        when (it) {
                            RelatedImagesListAdapter.Progress.SUCCESS -> {
                                relatedProgressBar.visibility = View.GONE
                                image.relatedImagesListAdapter = relatedImagesAdapter
                            }
                            RelatedImagesListAdapter.Progress.IN_PROGRESS -> {
                                setRelatedVisibility(true)
                                relatedProgressBar.visibility = View.VISIBLE
                                noRelatedFouundLabel.visibility = View.GONE
                                image.isRelatedOpened = true
                            }
                            RelatedImagesListAdapter.Progress.FAILED -> {
                                relatedProgressBar.visibility = View.GONE
                                noRelatedFouundLabel.visibility = View.VISIBLE
                                image.isRelatedOpened = false
                            }
                            RelatedImagesListAdapter.Progress.NO_RELATED_FOUND -> {
                                noRelatedFouundLabel.visibility = View.VISIBLE
                                image.isRelatedOpened = true
                                relatedProgressBar.visibility = View.GONE
                            }
                            null -> {
                            }
                        }
                    })
        }
    }

    companion object {
        private const val TAG = "Image View Holder"
    }
}
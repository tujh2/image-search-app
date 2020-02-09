package com.wnp.imagesearch.relatedImagesList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wnp.imagesearch.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.IOException

class RelatedImagesListAdapter : RecyclerView.Adapter<RelatedImagesViewHolder>() {
    private val relatedImagesList = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedImagesViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.related_image_list_item, parent, false)

        return RelatedImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RelatedImagesViewHolder, position: Int) {
        holder.bind(relatedImagesList[position])
    }

    override fun getItemCount(): Int = relatedImagesList.size

    fun getRelatedImages(siteUrl: String) {
        GlobalScope.launch {
            try {
                relatedImagesList.clear()
                val htmlDoc = Jsoup.connect(siteUrl).get()
                val relatedImages = htmlDoc.select("img")
                for (i in 0 until relatedImages.size) {
                    val img = relatedImages[i].attr("src")
                    if (img.isNotEmpty()) {
                        if (img[0] == '/' && img[1] == '/') {
                            relatedImagesList.add("http:$img")
                        } else if (img[0] == '/')
                            relatedImagesList.add(htmlDoc.baseUri() + img)
                        else
                            relatedImagesList.add(img)
                    }
                }
                GlobalScope.launch(Dispatchers.Main) {
                    notifyDataSetChanged()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
package com.wnp.imagesearch.relatedImagesList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.wnp.imagesearch.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.IOException

class RelatedImagesListAdapter : RecyclerView.Adapter<RelatedImagesViewHolder>() {
    private val relatedImagesList = mutableListOf<String>()
    private val state = MutableLiveData<Progress>()

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

    fun loadRelatedImages(siteUrl: String): LiveData<Progress> {
        getRelatedImages(siteUrl)
        return state
    }

    private fun getRelatedImages(siteUrl: String) {
        GlobalScope.launch {
            try {
                relatedImagesList.clear()
                val connection = Jsoup.connect(siteUrl)
                if(connection.response().statusCode() == 200) {
                    val htmlDoc = connection.get()
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
                        state.value = Progress.SUCESS
                        notifyDataSetChanged()
                    }
                } else {
                    state.value = Progress.FAILED
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    enum class Progress {
        SUCESS,
        FAILED
    }
}
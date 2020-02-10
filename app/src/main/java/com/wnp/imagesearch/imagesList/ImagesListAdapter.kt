package com.wnp.imagesearch.imagesList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wnp.imagesearch.GlideApp
import com.wnp.imagesearch.R
import com.wnp.imagesearch.RepoApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException

class ImagesListAdapter: RecyclerView.Adapter<ImagesListViewHolder>() {
    private val TAG = "Images List Adapter"
    private val imagesList = mutableListOf<Image>()
    private val api = RepoApp.getApi()
    private var page = 0
    private lateinit var searchQuery: String
    private val state = MutableLiveData<Progress>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.search_list_item, parent, false)
        val holder = ImagesListViewHolder(view)
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            holder.openRelated(imagesList[pos])
        }
        return holder
    }

    override fun onBindViewHolder(holder: ImagesListViewHolder, position: Int) {
        holder.bind(imagesList[position])
        Log.d(TAG, position.toString())
        if (position == imagesList.size / 2) {
            getImages()
        }
    }

    override fun getItemCount(): Int = imagesList.size



    fun loadImages(query: String): LiveData<Progress> {
        page = 0
        imagesList.clear()
        searchQuery = query
        getImages()
        return state
    }

    private fun getImages(){
        GlobalScope.launch {
            try {
                val body = api.requestImages(searchQuery, page).execute().body()?.string()
                val htmlDoc = Jsoup.parse(body)
                val jsonImages = htmlDoc.select("div.rg_meta.notranslate")
                for (i in 0 until jsonImages.size) {
                    val jsonImage = JSONObject(jsonImages[i].ownText())
                    val imgUrl = jsonImage.getString("ou")
                    val descr = jsonImage.getString("pt")
                    val siteUrl = jsonImage.getString("ru")
                    val thumbnailUrl = jsonImage.getString("tu")
                    val width = jsonImage.getInt("ow")
                    val height = jsonImage.getInt("oh")
                    imagesList.add(
                        Image(
                            imgUrl,
                            descr,
                            siteUrl,
                            thumbnailUrl,
                            width,
                            height
                        )
                    )
                }
                page += jsonImages.size
                GlobalScope.launch(Dispatchers.Main) {
                    state.value = Progress.SUCCESS
                    notifyDataSetChanged()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    enum class Progress {
        SUCCESS,
        FAILED
    }
}
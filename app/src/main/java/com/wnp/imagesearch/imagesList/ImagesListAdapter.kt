package com.wnp.imagesearch.imagesList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.wnp.imagesearch.R
import com.wnp.imagesearch.RepoApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ImagesListAdapter: RecyclerView.Adapter<ImagesListViewHolder>() {
    private val imagesList = mutableListOf<Image>()
    private val api = RepoApp.getApi()
    private var page = 0
    private lateinit var searchQuery: String
    private val state = MutableLiveData<Progress>()
    private var screenWidth: Int = 0

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
        holder.bind(imagesList[position], screenWidth)
        Log.d(TAG, position.toString())
        if (position == imagesList.size / 2) {
            getImages()
        }
    }

    override fun getItemCount(): Int = imagesList.size

    fun setScreenWidth(width: Int) {
        screenWidth = width
    }

    fun loadImages(query: String): LiveData<Progress> {
        page = 0
        imagesList.clear()
        searchQuery = query
        state.value = Progress.IN_PROGRESS
        getImages()
        return state
    }

    private fun getImages() {
        api.requestImages(searchQuery, page).enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                state.value = Progress.FAILED
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    val body = response.body()?.string()
                    GlobalScope.launch {
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
                            GlobalScope.launch(Dispatchers.Main){
                                state.value = Progress.SUCCESS
                                notifyItemInserted(imagesList.size - 1)
                            }
                        }
                        page += 20
                    }
                } else {
                    state.value = Progress.FAILED
                }
            }
        })
    }

    enum class Progress {
        SUCCESS,
        IN_PROGRESS,
        FAILED
    }

    companion object {
        private const val TAG = "Images List Adapter"
    }
}
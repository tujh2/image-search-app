package com.wnp.imagesearch.relatedImagesList

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.wnp.imagesearch.GlideApp
import com.wnp.imagesearch.R
import com.wnp.imagesearch.RepoApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Callback
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Response


const val MIN_RELATED_WIDTH = 250
const val MIN_RELATED_HEIGHT = 250

class RelatedImagesListAdapter : RecyclerView.Adapter<RelatedImagesViewHolder>() {
    private val relatedImagesList = mutableListOf<RelatedImage>()
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
        state.value = Progress.IN_PROGRESS
        getRelatedImages(siteUrl)
        return state
    }

    private fun getRelatedImages(siteUrl: String) {
        relatedImagesList.clear()
        val api = RepoApp.getApi()
        val appContext = RepoApp.gerAppContext()
        api.requestByUrl(siteUrl).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    GlobalScope.launch {
                        val body = response.body()?.string()
                        relatedImagesList.clear()
                        val htmlDoc = Jsoup.parse(body)
                        val relatedImages = htmlDoc.select("img")
                        val foundSize = relatedImages.size
                        for (i in 0 until foundSize) {
                            val img = relatedImages[i].attr("src")
                            if (img.isNotEmpty()) {
                                var imageUrl: String
                                if (img[0] == '/' && img[1] == '/') {
                                    imageUrl = "http:$img"
                                } else if (img[0] == '/')
                                    imageUrl = htmlDoc.baseUri() + img
                                else
                                    imageUrl = img
                               getRelatedImage(imageUrl, appContext, foundSize, i)
                            }
                        }
                    }
                } else {
                    state.value = Progress.FAILED
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                state.value = Progress.FAILED
            }
        })
    }

    private fun getRelatedImage(imageUrl: String,
                                appContext: Context,
                                relatedImagesSize: Int,
                                iter: Int) {
        GlideApp.with(appContext)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val width = resource.width
                    val height = resource.height
                    if (width >= MIN_RELATED_WIDTH && height >= MIN_RELATED_HEIGHT) {
                        relatedImagesList.add(
                            RelatedImage(
                                imageUrl,
                                width,
                                height
                            )
                        )
                        GlobalScope.launch(Dispatchers.Main) {
                            state.value = Progress.SUCCESS
                            notifyItemInserted(relatedImagesList.size - 1)
                        }
                    } else if(iter == relatedImagesSize - 1  && relatedImagesList.isEmpty()) {
                        state.value = Progress.NO_RELATED_FOUND
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    if(iter == relatedImagesSize - 1 && relatedImagesList.isEmpty())
                        state.value = Progress.NO_RELATED_FOUND
                }
            })
    }

    companion object {
        private const val TAG = "RelatedImagesAdapter"
    }

    enum class Progress {
        SUCCESS,
        IN_PROGRESS,
        NO_RELATED_FOUND,
        FAILED
    }
}
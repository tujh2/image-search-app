package com.wnp.imagesearch

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException

class SearchFragment : Fragment() {
    private var searchView: SearchView? = null
    private val TAG = "SearchFragment"
    private val imagesList = mutableListOf<Image>()
    private lateinit var listAdapter: ImagesListAdapter
    private lateinit var recyclerView: RecyclerView
    private var searchQuery: String = ""
    private var page = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        listAdapter = ImagesListAdapter()
        recyclerView = view.findViewById(R.id.images_list_main)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = listAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        searchView = menu.findItem(R.id.search_field)?.actionView as SearchView
        searchView?.setIconifiedByDefault(false)
        searchView?.requestFocus()
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                page = 0
                if (query != null && query.isNotEmpty()) {
                    imagesList.clear()
                    searchQuery = query
                    GlobalScope.launch {
                        getImages()
                    }
                }
                return when (query != null && query.isNotEmpty()) {
                    true -> false
                    false -> true
                }
            }
        })
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getImages() {
        try {
            val api = RepoApp.from(context as Context).getApi()
            val body = api.requestImages(searchQuery, page).execute().body()?.string()
            val htmlDoc = Jsoup.parse(body)
            val jsonImages = htmlDoc.select("div.rg_meta.notranslate")
            for (i in 0 until jsonImages.size) {
                val jsonImage = JSONObject(jsonImages[i].ownText())
                val imgUrl = jsonImage.getString("ou")
                val descr = jsonImage.getString("pt")
                val siteUrl = jsonImage.getString("ru")
                imagesList.add(Image(imgUrl, descr, siteUrl))
            }
            page += 100
            GlobalScope.launch(Dispatchers.Main) {
                listAdapter.notifyDataSetChanged()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    inner class ImagesListAdapter : RecyclerView.Adapter<ImagesListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesListViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.search_list_item, parent, false)

            val holder = ImagesListViewHolder(view)
            holder.itemView.setOnClickListener {
            }
            return holder
        }

        override fun onBindViewHolder(holder: ImagesListViewHolder, position: Int) {
            holder.bind(imagesList[position])
            Log.d(TAG, position.toString())
            if (position == imagesList.size / 2) {
                GlobalScope.launch {
                    getImages()
                }
            }
        }

        override fun getItemCount(): Int = imagesList.size
    }
}
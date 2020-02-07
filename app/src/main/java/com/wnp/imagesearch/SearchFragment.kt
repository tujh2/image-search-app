package com.wnp.imagesearch

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wnp.imagesearch.list.Image
import com.wnp.imagesearch.list.ImagesListAdapter
import com.wnp.imagesearch.network.ImagesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import java.io.IOException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET

class SearchFragment : Fragment() {
    private var searchView: SearchView? = null
    private val TAG = "SearchFragment"
    private val URL = "https://www.google.com/search?tbm=isch&q="
    private val imagesList = mutableListOf<Image>()
    private lateinit var listAdapter: ImagesListAdapter
    private lateinit var recyclerView: RecyclerView

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
                GlobalScope.launch {
                    if (query != null && query.isNotEmpty())
                        getImages(query)
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

    private fun getImages(searchQuery: String) {
        try {
            val api = ImagesApi().getApi()
            val body = api.requestImages(searchQuery).execute().body()?.string()
            val htmlDoc = Jsoup.parse(body)
            val table = htmlDoc.select("table")[4]
            val rows = table.select("tr")
            for (i in 0 until rows.size) {
                val row = rows[i]
                val cols = row.select("td")
                for (j in 0 until cols.size) {
                    val item = cols[j]
                    val siteUrl = HttpUrl
                        .parse("https://google.com" + item.select("a").attr("href"))
                        ?.queryParameter("q").toString()
                    val imgUrl = "https" + item.select("img").attr("src").substring(4)
                    val descr = item.select("font").text()
//                    Log.d(TAG, imgUrl)
//                    Log.d(TAG, siteUrl)
//                    Log.d(TAG, descr)
                    //Log.d(TAG, imgUrl)
                    imagesList.add(Image(imgUrl, descr, siteUrl))
                }
                GlobalScope.launch(Dispatchers.Main) {
                    listAdapter.setData(imagesList)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
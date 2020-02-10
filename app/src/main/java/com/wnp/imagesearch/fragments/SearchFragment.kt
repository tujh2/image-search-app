package com.wnp.imagesearch.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wnp.imagesearch.R
import com.wnp.imagesearch.imagesList.ImagesListAdapter

class SearchFragment : Fragment() {
    private val TAG = "SearchFragment"
    private val listAdapter = ImagesListAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.images_list_main)
        progressBar = view.findViewById(R.id.progress_images_load)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = listAdapter
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        listAdapter.setScreenWidth(displayMetrics.widthPixels)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        searchView = menu.findItem(R.id.search_field)?.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        if (searchQuery.isNotEmpty()) {
            searchView.setIconifiedByDefault(false)
            searchView.setQuery(searchQuery, false)
        } else {
            searchView.isIconified = false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    searchQuery = query
                    progressBar.visibility = View.VISIBLE
                    listAdapter.loadImages(query)
                        .observe(viewLifecycleOwner, Observer<ImagesListAdapter.Progress> {
                            when (it) {
                                ImagesListAdapter.Progress.IN_PROGRESS -> {
                                    progressBar.visibility = View.VISIBLE
                                    searchView.clearFocus()
                                }
                                ImagesListAdapter.Progress.SUCCESS -> {
                                    progressBar.visibility = View.GONE
                                }
                                ImagesListAdapter.Progress.FAILED -> {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        context,
                                        "Connection error, try again",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                null -> {
                                }
                            }
                        })
                }
                return when (query != null && query.isNotEmpty()) {
                    true -> false
                    false -> true
                }
            }
        })
    }
}
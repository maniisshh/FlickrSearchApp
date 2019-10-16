package com.manisks.flickrsearchapp

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.manisks.flickrsearchapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var searchView: SearchView
    private var mQueryString: String = ""
    private var mHandler: Handler = Handler()
    private lateinit var mAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupList()
        setupDummyList()
    }

    private fun setupDummyList() {
        mAdapter.data= arrayListOf("a","b","b","b","b","b","b","b","b","b","b","b","b","b","b","b")
    }

    private fun setupList() {
        binding.rvImageList.layoutManager = GridLayoutManager(this, 5)
        mAdapter = ImageAdapter()
        binding.rvImageList.adapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu!!.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search Images"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                mQueryString = newText
                mHandler.removeCallbacksAndMessages(null)
                mHandler.postDelayed({
                    getImagesFromFlickr()
                }, 500)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun getImagesFromFlickr() {
    }
}
package com.manisks.flickrsearchapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manisks.flickrsearchapp.databinding.ActivityMainBinding
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var searchView: SearchView
    private var mQueryString: String = ""
    private var mHandler: Handler = Handler()
    private lateinit var mAdapter: ImageAdapter
    private var columnCount = 2
    private lateinit var mLayoutManager: GridLayoutManager

    /**
     * Lazily initialize our [MainViewModel].
     */
    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupList()

        binding.btnSubmit.setOnClickListener {
            val column = binding.etNumber.text.toString().toInt()
            if (column in 1..5) {
                columnCount=column
                mLayoutManager.spanCount=columnCount
                mLayoutManager.requestLayout()
                hideSoftKeyboard(searchView)
            } else {
                val snack = Snackbar.make(
                    binding.etNumber,
                    "Choose a number between 1 to 5", Snackbar.LENGTH_LONG
                )
                binding.etNumber.error = "Enter a valid number"
                snack.show()
            }

        }

        viewModel.photoList.observe(this, Observer {
            it?.let {
                mAdapter.data = it
            }
        })

        binding.lifecycleOwner = this
    }

    /**
     * Method to setup the image list
     */
    private fun setupList() {
        mLayoutManager = GridLayoutManager(this, columnCount)
        binding.rvImageList.layoutManager = mLayoutManager
        mAdapter = ImageAdapter(this)
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
                    viewModel.getPhotosFromFlickr(newText)
                    hideSoftKeyboard(searchView)
                }, 500)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

}
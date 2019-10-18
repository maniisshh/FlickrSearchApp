package com.manisks.flickrsearchapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manisks.flickrsearchapp.ImageAdapter.OnBottomReachedListener
import com.manisks.flickrsearchapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var searchView: SearchView
    private var mQueryString: String = ""
    private var mHandler: Handler = Handler()
    private lateinit var mAdapter: ImageAdapter
    private var columnCount = 2
    private lateinit var mLayoutManager: GridLayoutManager
    private lateinit var photos: FlickrResult.Photos
    private var currentPage = 1
    private var serchText = ""

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
            var msg = ""
            if (column in 1..5) {
                columnCount = column
                mLayoutManager.spanCount = columnCount
                mLayoutManager.requestLayout()
                hideKeyboard()
                msg = "You have selected $column items per row"
                binding.etNumber.setText("")
            } else {
                msg = "Choose a number between 1 to 5"
                binding.etNumber.error = "Enter a valid number"
            }

            val snack = Snackbar.make(
                binding.etNumber,
                msg, Snackbar.LENGTH_LONG
            )
            snack.show()
        }

        mAdapter.setOnBottomReachedListener(object : OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                binding.btnViewMore.visibility = View.VISIBLE
            }
        })

        viewModel.photoList.observe(this, Observer {
            it?.let {
                if (currentPage > 1) {
                    mAdapter.data = mAdapter.data.plus(it)
                    binding.progressBar.visibility = View.GONE
                } else {
                    mAdapter.data = it
                }
            }
        })

        viewModel.photos.observe(this, Observer {
            it?.let {
                photos = it
                currentPage = photos.page
            }
        })

        viewModel.message.observe(this, Observer {
            val snack = Snackbar.make(
                binding.etNumber,
                it, Snackbar.LENGTH_LONG
            )
            if (it.isNotEmpty()) snack.show()
        })

        binding.btnViewMore.setOnClickListener {
            viewModel.getPhotosFromFlickr(serchText, photos.page + 1)
            binding.progressBar.visibility = View.VISIBLE
            it.visibility = View.GONE
        }

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
                if (newText.isNotEmpty()) {
                    mHandler.postDelayed({
                        serchText = newText
                        currentPage = 1
                        viewModel.getPhotosFromFlickr(newText, 1)
                        hideKeyboard()
                        binding.btnViewMore.visibility = View.GONE
                    }, 500)
                }
                return true
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }

}
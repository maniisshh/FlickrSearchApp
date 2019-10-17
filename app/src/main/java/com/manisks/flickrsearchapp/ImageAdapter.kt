package com.manisks.flickrsearchapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Created by user on 14-10-2019.
 */
class ImageAdapter(val mContext: Context) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    var data = listOf<FlickrResult.Photos.Photo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photoModel: FlickrResult.Photos.Photo = data[position]

        Glide
            .with(mContext)
            .load(getPhotoURL(photoModel.farm, photoModel.id, photoModel.secret, photoModel.server))
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.photo)

    }

    private fun getPhotoURL(
        farm: Int,
        id: String,
        secret: String,
        server: String
    ): String {
        return "https://farm$farm.static.flickr.com/$server/$id/_$secret.jpg"
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageView = itemView.findViewById(R.id.ivPhoto)
    }
}
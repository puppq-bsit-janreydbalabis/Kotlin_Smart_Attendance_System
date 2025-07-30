package com.example.attendance_system

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class RecentPhotosAdapter(
    private val photos: List<Uri>,
    private val onPhotoClick: (Uri) -> Unit
) : RecyclerView.Adapter<RecentPhotosAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.recentPhotoImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUri = photos[position]
        
        // Check if context is still valid
        val context = holder.itemView.context
        if (context == null) return
        
        // Load image using Glide with better resource management
        Glide.with(context)
            .load(photoUri)
            .apply(RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_gallery)
                .error(R.drawable.ic_gallery)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .timeout(10000)) // 10 second timeout
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onPhotoClick(photoUri)
        }
    }

    override fun getItemCount(): Int = photos.size
    
    override fun onViewRecycled(holder: PhotoViewHolder) {
        super.onViewRecycled(holder)
        // Clear the image when view is recycled to prevent memory leaks
        Glide.with(holder.itemView.context).clear(holder.imageView)
    }
} 
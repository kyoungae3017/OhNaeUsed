package com.kyoungae.ohnaejunggo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.data.Gallery
import com.kyoungae.ohnaejunggo.data.Image
import com.kyoungae.ohnaejunggo.databinding.ItemListGalleryBinding
import com.kyoungae.ohnaejunggo.databinding.ItemListPickedPhotoBinding
import com.kyoungae.ohnaejunggo.viewmodel.GalleryViewModel
import com.kyoungae.ohnaejunggo.viewmodel.ProductWritingViewModel

class PickedPhotoAdapter(
    private val viewModel: ProductWritingViewModel
) : ListAdapter<Image, PickedPhotoAdapter.ViewHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListPickedPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item) {
            Log.d(TAG, "onBindViewHolder: $position")
            viewModel.removePickedPhoto(position)
            notifyDataSetChanged()
        }
    }

    class ViewHolder(private val binding: ItemListPickedPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Image,
            itemClicked: (String?) -> Unit
        ) {

            binding.clearButton.setOnClickListener {
                itemClicked(null)
            }

            if (item.isVisible) {
                binding.parentLayout.visibility = View.VISIBLE

                var loadImage: String

                if (item.localPathName.isNullOrEmpty()) {
                    loadImage = item.url!!
                }else{
                    loadImage = item.localPathName
                }

                Glide.with(binding.root)
                    .load(loadImage)
                    .into(binding.imageView)

            } else {
                binding.parentLayout.visibility = View.GONE
                val params: ViewGroup.LayoutParams = binding.parentLayout.layoutParams
                params.height = 0
                params.width = 0
                binding.parentLayout.layoutParams = params
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object {
        private const val TAG = "GalleryAdapter"
    }
}

class DiffUtil : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.localPathName == newItem.localPathName
    }

}

package com.kyoungae.ohnaejunggo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.data.Gallery
import com.kyoungae.ohnaejunggo.databinding.ItemListGalleryBinding
import com.kyoungae.ohnaejunggo.viewmodel.GalleryViewModel

class GalleryAdapter(
    private val viewModel: GalleryViewModel
) : ListAdapter<Gallery, GalleryAdapter.GalleryViewHolder>(GalleryDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            ItemListGalleryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), viewModel
        )
    }

    override fun onBindViewHolder(holderGallery: GalleryViewHolder, position: Int) {
        val item = getItem(position)
        holderGallery.bind(item) {
            viewModel.setValidValue()
            notifyDataSetChanged()
        }
    }

    class GalleryViewHolder(
        private val binding: ItemListGalleryBinding,
        private val viewModel: GalleryViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Gallery,
            itemClicked: (String?) -> Unit
        ) {

            if (item.isClicked) {
                binding.imageView.setBackgroundResource(R.drawable.rectangle_teal)
            } else {
                binding.imageView.setBackgroundColor(0)
            }

            binding.imageView.setOnClickListener {
//                if (viewModel.isSelectable()) {
                if (item.isClicked) {
                    item.isClicked = false
                    itemClicked(null)
                } else {
                    if (viewModel.isSelectable()) {
                        item.isClicked = true
                        itemClicked(null)
                    } else {
                        Toast.makeText(binding.root.context, "더 이상 선택 할 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            Glide.with(binding.root)
                .load(item.contentUri)
                .into(binding.imageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class GalleryDiffUtil : DiffUtil.ItemCallback<Gallery>() {
    override fun areItemsTheSame(oldItem: Gallery, newItem: Gallery): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Gallery, newItem: Gallery): Boolean {
        return oldItem.isClicked == newItem.isClicked
    }

}

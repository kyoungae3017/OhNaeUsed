package com.kyoungae.ohnaejunggo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.databinding.ItemListProductImageBinding

class ProductDetailViewPagerAdapter(val list: MutableList<String>) :
    RecyclerView.Adapter<ProductDetailViewPagerAdapter.PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            ItemListProductImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

    inner class PagerViewHolder(val binding: ItemListProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            Glide.with(binding.root)
                .load(item)
                .into(binding.imageView)
        }
    }
}
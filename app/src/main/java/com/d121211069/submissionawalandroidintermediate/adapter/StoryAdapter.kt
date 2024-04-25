package com.d121211069.submissionawalandroidintermediate.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.d121211069.submissionawalandroidintermediate.data.local.entity.StoryEntity
import com.d121211069.submissionawalandroidintermediate.databinding.ItemStoryBinding
import com.d121211069.submissionawalandroidintermediate.view.DetailActivity

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryEntity) {
            binding.storyName.text = item.name
            binding.storyDesc.text = item.desc
            Glide.with(itemView.context).load(item.photoUrl).into(binding.storyImage)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.storyImage, "profile"),
                        Pair(binding.storyName, "name"),
                        Pair(binding.storyDesc, "description"),
                    )
                intent.putExtra(DATA, item)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        private const val DATA = "data_detail"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryEntity, newItem: StoryEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
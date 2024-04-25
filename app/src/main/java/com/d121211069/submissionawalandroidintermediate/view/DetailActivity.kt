package com.d121211069.submissionawalandroidintermediate.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.d121211069.submissionawalandroidintermediate.data.local.entity.StoryEntity
import com.d121211069.submissionawalandroidintermediate.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val data = intent.getParcelableExtra<StoryEntity>(DATA)

        if (data != null) {
            binding.detailDesc.text = data.desc
            binding.detailTitle.text = data.name
            Glide.with(this)
                .load(data.photoUrl)
                .into(binding.detailImage)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        private const val DATA = "data_detail"
    }
}
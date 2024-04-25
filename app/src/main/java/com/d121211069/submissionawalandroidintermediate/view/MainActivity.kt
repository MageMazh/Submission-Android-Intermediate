package com.d121211069.submissionawalandroidintermediate.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.d121211069.submissionawalandroidintermediate.R
import com.d121211069.submissionawalandroidintermediate.adapter.LoadingStateAdapter
import com.d121211069.submissionawalandroidintermediate.adapter.StoryAdapter
import com.d121211069.submissionawalandroidintermediate.databinding.ActivityMainBinding
import com.d121211069.submissionawalandroidintermediate.factory.ViewModelFactory
import com.d121211069.submissionawalandroidintermediate.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        mainViewModel.getSession().observe(this) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token

                val adapter = StoryAdapter()
                binding.rvStory.adapter =
                    adapter.withLoadStateHeaderAndFooter(footer = LoadingStateAdapter {
                        adapter.retry()
                    }, header = LoadingStateAdapter {
                        adapter.retry()
                    })

                mainViewModel.getAllStories(token).observe(this, {
                    adapter.submitData(lifecycle, it)
                })
            }
        }

        binding.buttonAdd?.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Logout")
                        setMessage(getString(R.string.info_logout))
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            mainViewModel.logout()
                        }
                        setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
                        create()
                        show()
                    }
                    true
                }

                R.id.action_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                R.id.action_map -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getAllStories(token)
    }
}
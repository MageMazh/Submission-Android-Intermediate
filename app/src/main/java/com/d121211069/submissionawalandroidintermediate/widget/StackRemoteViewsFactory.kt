package com.d121211069.submissionawalandroidintermediate.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.d121211069.submissionawalandroidintermediate.R
import com.d121211069.submissionawalandroidintermediate.data.remote.response.ListStoryItem
import com.d121211069.submissionawalandroidintermediate.data.remote.response.StoryResponse
import com.d121211069.submissionawalandroidintermediate.data.remote.retrofit.ApiConfig
import com.d121211069.submissionawalandroidintermediate.datastore.UserPreferences
import com.d121211069.submissionawalandroidintermediate.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val pref = UserPreferences.getInstance(mContext.dataStore)
    private val user = runBlocking { pref.getSession().first() }

    private val mWidgetItems = ArrayList<Bitmap>()

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        fetchStoryImages()
    }

    private fun fetchStoryImages() {
        val client = ApiConfig.getApiService().getWidgetStories("Bearer " + user.token)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>, response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    handleApiResponse(response.body()?.listStory as List<ListStoryItem>)
                } else {
                    Log.d("widget", response.message().toString())
                }
            }

            override fun onFailure(
                call: Call<StoryResponse>, t: Throwable
            ) {
                Log.d("widget", t.message.toString())
            }
        })
    }

    private fun handleApiResponse(storyImages: List<ListStoryItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                mWidgetItems.clear()

                for (storyImage in storyImages) {
                    val imageUrl = storyImage.photoUrl

                    val bitmap = Glide.with(mContext).asBitmap().load(imageUrl).submit().get()
                    mWidgetItems.add(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            StoryListWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}
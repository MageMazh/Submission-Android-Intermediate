package com.d121211069.submissionawalandroidintermediate.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
data class StoryEntity(
    @PrimaryKey val id: String,
    val photoUrl: String,
    val createdAt: String? = null,
    val name: String,
    val desc: String? = null,
    val lon: Double? = null,
    val lat: Double? = null,
) : Parcelable
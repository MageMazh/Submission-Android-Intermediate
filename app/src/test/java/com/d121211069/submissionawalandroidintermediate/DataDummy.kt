package com.d121211069.submissionawalandroidintermediate

import com.d121211069.submissionawalandroidintermediate.data.local.entity.StoryEntity

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                i.toString(),
                "photoUrl = $i",
                "createdAt $i",
                "name $i",
                "desc $i",
                i.toDouble(),
                i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}
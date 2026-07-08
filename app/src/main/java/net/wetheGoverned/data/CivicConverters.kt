package net.wetheGoverned.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.wetheGoverned.model.PollOption
import net.wetheGoverned.model.PollPost

class CivicConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromPollOptions(value: List<PollOption>): String = json.encodeToString(value)

    @TypeConverter
    fun toPollOptions(value: String): List<PollOption> = json.decodeFromString(value)

    @TypeConverter
    fun fromPollPosts(value: List<PollPost>): String = json.encodeToString(value)

    @TypeConverter
    fun toPollPosts(value: String): List<PollPost> = json.decodeFromString(value)

    @TypeConverter
    fun fromDistrictBreakdown(value: Map<String, Map<String, Int>>?): String? = 
        value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toDistrictBreakdown(value: String?): Map<String, Map<String, Int>>? = 
        value?.let { json.decodeFromString(it) }
}

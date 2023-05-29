package b.gaytan.app.data.local

import androidx.room.TypeConverter
import b.gaytan.app.data.model.ThumbnailModel
import com.google.gson.Gson

class MarvelConverters {
    @TypeConverter
    fun fromThumbnails(thumbnailModel: ThumbnailModel): String = Gson().toJson(thumbnailModel)

    @TypeConverter
    fun toThumbnail(thumbnailModel: String): ThumbnailModel =
        Gson().fromJson(thumbnailModel, ThumbnailModel::class.java)
}
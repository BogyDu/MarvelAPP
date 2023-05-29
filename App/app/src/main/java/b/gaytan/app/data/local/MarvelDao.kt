package b.gaytan.app.data.local

import androidx.room.*
import b.gaytan.app.data.model.character.CharacterModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MarvelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(characterModel: CharacterModel): Long

    @Query("SELECT * FROM characterModel")
    fun getAll(): Flow<List<CharacterModel>>

    @Delete
    suspend fun delete(characterModel: CharacterModel)
}
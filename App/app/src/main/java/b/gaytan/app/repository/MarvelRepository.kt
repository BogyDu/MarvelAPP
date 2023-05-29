package b.gaytan.app.repository

import b.gaytan.app.data.local.MarvelDao
import b.gaytan.app.data.model.character.CharacterModel
import b.gaytan.app.data.remote.Service
import javax.inject.Inject

class MarvelRepository @Inject constructor(
    private val api: Service,
    private val dao: MarvelDao
) {
    suspend fun list(nameStartsWith: String? = null) = api.list(nameStartsWith)
    suspend fun getComics(characterId: Int) = api.getComics(characterId)

    suspend fun insert(characterModel: CharacterModel) = dao.insert(characterModel)
    fun getAll() = dao.getAll()
    suspend fun delete(characterModel: CharacterModel) = dao.delete(characterModel)
}
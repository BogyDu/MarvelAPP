package b.gaytan.app.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import b.gaytan.app.data.model.character.CharacterModel
import b.gaytan.app.data.model.comic.ComicModelResponse
import b.gaytan.app.repository.MarvelRepository
import b.gaytan.app.ui.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailsCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
) : ViewModel() {

    private val _details =
        MutableStateFlow<ResourceState<ComicModelResponse>>(ResourceState.Empty())
    val searchContainer: StateFlow<ResourceState<ComicModelResponse>> = _details

    fun fetch(characterId: Int) = viewModelScope.launch {
        safeFetch(characterId)
    }

    private suspend fun safeFetch(characterId: Int) {
        _details.value = ResourceState.Loading()
        try {
            val response = repository.getComics(characterId)
            _details.value = handleResponse(response)
        } catch (t: Throwable) {
            val response = repository.getComics(characterId)
            when (t) {
                is IOException -> _details.value =
                    ResourceState.Error("Erro de conexão", response.body())
                else -> _details.value = ResourceState.Error("erro na conversão", response.body())
            }
        }
    }

    private fun handleResponse(response: Response<ComicModelResponse>): ResourceState<ComicModelResponse> {
        if (response.isSuccessful) {
            response.body()?.let { values ->
                return ResourceState.Success(values)
            }
        }
        return ResourceState.Error(response.message(), response.body())
    }

    fun insert(characterModel: CharacterModel) = viewModelScope.launch {
        repository.insert(characterModel)
    }

}
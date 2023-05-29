package b.gaytan.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import b.gaytan.app.data.model.character.CharacterModelResponse
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
class SearchCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
) : ViewModel() {

    private val _searchCharacter =
        MutableStateFlow<ResourceState<CharacterModelResponse>>(ResourceState.Empty())
    val searchContainer: StateFlow<ResourceState<CharacterModelResponse>> = _searchCharacter

    fun fetch(nameStartsWith: String) = viewModelScope.launch {
        safeFetch(nameStartsWith)
    }

    private suspend fun safeFetch(nameStartsWith: String) {
        _searchCharacter.value = ResourceState.Loading()
        try {
            val response = repository.list(nameStartsWith)
            _searchCharacter.value = handleResponse(response)
        } catch (t: Throwable) {
            val response = repository.list(nameStartsWith)
            when (t) {
                is IOException -> _searchCharacter.value =
                    ResourceState.Error("Erro na conexão", response.body())
                else -> _searchCharacter.value =
                    ResourceState.Error("Erro na conversão", response.body())
            }

        }

    }

    private fun handleResponse(response: Response<CharacterModelResponse>): ResourceState<CharacterModelResponse> {
        if (response.isSuccessful) {
            response.body()?.let { values ->
                return ResourceState.Success(values)
            }
        }
        return ResourceState.Error(response.message(), response.body())
    }
}
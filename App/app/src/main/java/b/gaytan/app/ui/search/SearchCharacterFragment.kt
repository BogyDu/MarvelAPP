package b.gaytan.app.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import b.gaytan.app.Constants.DEFAULT_QUERY
import b.gaytan.app.Constants.LAST_SEARCH_QUERY
import b.gaytan.app.R
import b.gaytan.app.databinding.FragmentSearchCharacterBinding
import b.gaytan.app.ui.adapters.CharacterAdapter
import b.gaytan.app.ui.base.BaseFragment
import b.gaytan.app.ui.state.ResourceState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SearchCharacterFragment :
    BaseFragment<FragmentSearchCharacterBinding, SearchCharacterViewModel>() {
    override val viewModel: SearchCharacterViewModel by viewModels()
    private val characterAdaper by lazy { CharacterAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        searchInit(query)

        collectObservers()
    }

    private fun collectObservers() = lifecycleScope.launch {
        viewModel.searchContainer.collect { result ->
            when (result) {
                is ResourceState.Success -> {
                    biding.progressbarSearch.visibility = View.INVISIBLE
                    result.data?.let {
                        characterAdaper.characters = it.data.results.toList()
                    }
                }
                is ResourceState.Error -> {
                    biding.progressbarSearch.visibility = View.INVISIBLE
                    result.message?.let { message ->
                        Timber.tag("SearchCharacterFragment").e("Error -> $message")
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.an_error_occurred),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                is ResourceState.Loading -> {
                    biding.progressbarSearch.visibility = View.VISIBLE
                }
                else -> {

                }

            }
        }
    }

    private fun searchInit(query: String) = with(biding) {
        edSearchCharacter.setText(query)
        edSearchCharacter.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateCharacterList()
                true
            } else {
                false
            }
        }

        edSearchCharacter.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateCharacterList()
                true
            } else {
                false
            }
        }
    }

    private fun updateCharacterList() = with(biding) {
        edSearchCharacter.editableText.trim().let {
            if (it.isNotEmpty()) {
                searchQuery(it.toString())
            }
        }

    }

    private fun searchQuery(query: String) {
        viewModel.fetch(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            LAST_SEARCH_QUERY,
            biding.edSearchCharacter.editableText.trim().toString()
        )
    }

    private fun setupRecyclerView() = with(biding) {
        rvSearchCharacter.apply {
            adapter = characterAdaper
            layoutManager = LinearLayoutManager(context)
        }

    }

    override fun getViewBiding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchCharacterBinding =
        FragmentSearchCharacterBinding.inflate(inflater, container, false)
}
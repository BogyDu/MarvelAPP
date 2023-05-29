package b.gaytan.app.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import b.gaytan.app.R
import b.gaytan.app.databinding.FragmentListCharacterBinding
import b.gaytan.app.ui.adapters.CharacterAdapter
import b.gaytan.app.ui.base.BaseFragment
import b.gaytan.app.ui.state.ResourceState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ListCharacterFragment : BaseFragment<FragmentListCharacterBinding, ListCharacterViewModel>() {
    override val viewModel: ListCharacterViewModel by viewModels()
    private val characterAdapter by lazy {
        CharacterAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickAdapter()
        colectObservsers()

    }

    private fun colectObservsers() = lifecycleScope.launch {
        viewModel.list.collect { resource ->
            when (resource) {
                is ResourceState.Success -> {
                    resource.data?.let { values ->
                        biding.progressCircular.visibility = View.INVISIBLE
                        characterAdapter.characters = values.data.results.toList()
                    }
                }
                is ResourceState.Error -> {
                    biding.progressCircular.visibility = View.INVISIBLE
                    resource.message?.let { message ->
                        Toast.makeText(
                            requireContext(), getString(R.string.on_error),
                            Toast.LENGTH_SHORT
                        ).show()
                        Timber.tag("ListCharacterFragment").e("Error")
                    }
                }
                is ResourceState.Loading -> {
                    biding.progressCircular.visibility = View.VISIBLE
                }
                else -> {

                }
            }

        }
    }

    private fun setupRecyclerView() = with(biding) {
        rvCharacters.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun clickAdapter() {
        characterAdapter.setOnClickListener { characterModel ->
            val action = ListCharacterFragmentDirections
                .actionListCharacterFragmentToDetailsCharacterFragment(characterModel)
            findNavController().navigate(action)
        }
    }

    override fun getViewBiding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListCharacterBinding =
        FragmentListCharacterBinding.inflate(inflater, container, false)


}
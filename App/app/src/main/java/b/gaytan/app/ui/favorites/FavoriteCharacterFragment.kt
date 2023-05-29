package b.gaytan.app.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import b.gaytan.app.R
import b.gaytan.app.databinding.FragmentFavoriteCharacterBinding
import b.gaytan.app.ui.adapters.CharacterAdapter
import b.gaytan.app.ui.base.BaseFragment
import b.gaytan.app.ui.state.ResourceState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteCharacterFragment :
    BaseFragment<FragmentFavoriteCharacterBinding, FavoriteCharacterViewModel>() {
    override val viewModel: FavoriteCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickAdapters()
        observers()
    }

    private fun observers() = lifecycleScope.launch {
        viewModel.favorite.collect { resource ->
            when (resource) {
                is ResourceState.Success -> {
                    resource.data?.let {
                        biding.tvEmptyList.visibility = View.INVISIBLE
                        characterAdapter.characters = it.toList()
                    }
                }
                else -> {

                }

            }
        }
    }

    private fun clickAdapters() {
        characterAdapter.setOnClickListener { characterModel ->
            val action = FavoriteCharacterFragmentDirections
                .actionFavoriteCharacterFragmentToDetailsCharacterFragment(characterModel)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(biding) {
        rvFavoriteCharacter.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(context)
        }
        ItemTouchHelper(itemTouchHelperCallback())
            .attachToRecyclerView(rvFavoriteCharacter)
    }

    private fun itemTouchHelperCallback(): ItemTouchHelper.SimpleCallback {
        return object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val character = characterAdapter.getCharacterPosition(viewHolder.adapterPosition)
                viewModel.delete(character).also {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.message_delete_character),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun getViewBiding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteCharacterBinding =
        FragmentFavoriteCharacterBinding.inflate(inflater, container, false)
}
package b.gaytan.app.ui.details

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import b.gaytan.app.R
import b.gaytan.app.data.model.character.CharacterModel
import b.gaytan.app.databinding.FragmentDetailsCharacterBinding
import b.gaytan.app.ui.adapters.ComicAdapter
import b.gaytan.app.ui.base.BaseFragment
import b.gaytan.app.ui.state.ResourceState
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsCharacterFragment :
    BaseFragment<FragmentDetailsCharacterBinding, DetailsCharacterViewModel>() {
    override val viewModel: DetailsCharacterViewModel by viewModels()

    private val args: DetailsCharacterFragmentArgs by navArgs()
    private val comicAdapter by lazy { ComicAdapter() }
    private lateinit var characterModel: CharacterModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterModel = args.characterid
        viewModel.fetch(characterModel.id)
        setupRecyclerView()
        onLoadedCharacter(characterModel)
        collectObservers()
        biding.tvDescriptionCharacterDetails.setOnClickListener {
            onShowDialog(characterModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    private fun onShowDialog(characterModel: CharacterModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(characterModel.name)
            .setMessage(characterModel.description)
            .setNegativeButton(getString(R.string.close_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
            .setView(View.VISIBLE)
    }

    private fun collectObservers() = lifecycleScope.launch {
        viewModel.searchContainer.collect { result ->
            when (result) {
                is ResourceState.Success -> {
                    biding.progressBarDetail.visibility = View.INVISIBLE
                    result.data?.let { values ->
                        if (values.data.result.isNotEmpty()) {
                            comicAdapter.comics = values.data.result.toList()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.an_error_occurred),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
                is ResourceState.Error -> {
                    biding.progressBarDetail.visibility = View.INVISIBLE
                    result.message?.let { message ->
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.an_error_occurred),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                is ResourceState.Loading -> {
                    biding.progressBarDetail.visibility = View.VISIBLE
                }
                else -> {

                }

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> {
                viewModel.insert(characterModel)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.saved_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onLoadedCharacter(characterModel: CharacterModel) = with(biding) {
        tvNameCharacterDetails.text = characterModel.name
        if (characterModel.description.isEmpty()) {
            tvDescriptionCharacterDetails.text =
                requireContext().getString(R.string.text_description_empty)
        } else {
            tvDescriptionCharacterDetails.text = characterModel.description
        }

        Glide.with(requireContext())
            .load(characterModel.thumbnail.path + "." + characterModel.thumbnail.extension)
    }

    private fun setupRecyclerView() = with(biding) {
        rvComics.apply {
            adapter = comicAdapter
            layoutManager = LinearLayoutManager(context)

        }
    }

    override fun getViewBiding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsCharacterBinding =
        FragmentDetailsCharacterBinding.inflate(inflater, container, false)
}
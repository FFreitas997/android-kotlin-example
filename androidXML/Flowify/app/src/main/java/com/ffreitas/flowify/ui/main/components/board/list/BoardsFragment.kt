package com.ffreitas.flowify.ui.main.components.board.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.databinding.FragmentBoardsBinding
import com.ffreitas.flowify.ui.main.SharedViewModel
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardsFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog

    private var _binding: FragmentBoardsBinding? = null
    private val binding get() = _binding!!

    private val model by viewModels<BoardsViewModel>()
    private val shared by activityViewModels<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressDialog = ProgressDialog(requireActivity())

        model.state.observe(viewLifecycleOwner) { handleState(it) }

        shared.shouldFetchBoards.observe(viewLifecycleOwner) { shouldUpdate ->
            if (shouldUpdate)
                model.fetchBoards()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleState(state: BoardsState) {
        when (state) {
            is BoardsState.Loading -> {
                progressDialog.show()
            }

            is BoardsState.Success -> {
                progressDialog.dismiss()
                handleBoardList(state.boards)
            }

            is BoardsState.Error -> {
                progressDialog.dismiss()
                Log.e(TAG, "Error: ${state.message}")
                handleErrorMessage(R.string.boards_fragment_error_fetching_boards)
            }
        }
    }

    private fun handleBoardList(boards: List<Board>) {
        if (boards.isEmpty()) {
            binding.emptyBoardsList.visibility = View.VISIBLE
            binding.boardsList.visibility = View.GONE
            return
        }
        binding.emptyBoardsList.visibility = View.GONE
        binding.boardsList.visibility = View.VISIBLE
        val adapter = BoardsAdapter(requireContext(), boards)
        binding.boardsList.adapter = adapter
        binding.boardsList.setHasFixedSize(true)
        binding.boardsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter.setOnClickListener { _, board -> handleClickBoard(board) }
    }

    private fun handleClickBoard(board: Board) {
        Snackbar
            .make(binding.root, "Clicked on ${board.name}", Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Snackbar
            .make(binding.root, getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_error, null))
            .setTextColor(resources.getColor(R.color.md_theme_onError, null))
            .show()
    }

    companion object {
        const val TAG = "Boards Fragment"
    }
}
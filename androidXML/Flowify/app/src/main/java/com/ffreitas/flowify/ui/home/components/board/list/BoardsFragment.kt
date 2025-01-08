package com.ffreitas.flowify.ui.home.components.board.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ffreitas.flowify.databinding.FragmentBoardsBinding
import com.ffreitas.flowify.ui.home.SharedViewModel

class BoardsFragment : Fragment() {

    private var _binding: FragmentBoardsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model by viewModels<BoardsViewModel>()
    private val shared: SharedViewModel by activityViewModels { SharedViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.d(TAG, "Boards Fragment created with user: ${shared.currentUser}")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "Boards Fragment"
    }
}
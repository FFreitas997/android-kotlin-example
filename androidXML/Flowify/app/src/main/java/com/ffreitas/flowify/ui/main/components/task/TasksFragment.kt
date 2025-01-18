package com.ffreitas.flowify.ui.main.components.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.Task
import com.ffreitas.flowify.databinding.FragmentTasksBinding
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog

    private var _layout: FragmentTasksBinding? = null
    private val layout get() = _layout!!

    private val viewModel: TasksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _layout = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = layout.root

        progressDialog = ProgressDialog(requireActivity())

        viewModel.state.observe(viewLifecycleOwner) { handleState(it) }

        return root
    }

    override fun onStart() {
        super.onStart()
        viewModel.requestAllTasks()
    }

    private fun handleState(state: TasksState) {
        when (state) {
            is TasksState.Loading -> progressDialog.show()
            is TasksState.Error -> {
                progressDialog.dismiss()
                Log.e(TAG, state.message)
                handleErrorMessage(R.string.tasks_fragment_error_fetching_tasks)
            }

            is TasksState.Success -> {
                progressDialog.dismiss()
                handleTaskList(state.tasks)
            }
        }
    }

    private fun handleTaskList(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            layout.emptyTasksList.visibility = View.VISIBLE
            layout.tasksList.visibility = View.GONE
            return
        }
        layout.emptyTasksList.visibility = View.GONE
        layout.tasksList.visibility = View.VISIBLE

        val adapter = MainTasksAdapter(requireContext(), tasks.toMutableList())
        layout.tasksList.adapter = adapter
        layout.tasksList.setHasFixedSize(true)
        layout.tasksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter.setOnClickListener { task -> handleTaskClick(task) }
    }

    private fun handleTaskClick(task: Task) {
        AlertDialog
            .Builder(requireContext())
            .setTitle(task.title)
            .setMessage(task.description)
            .setCancelable(true)
            .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.info_24px))
            .show()
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Snackbar
            .make(layout.root, getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_error, null))
            .setTextColor(resources.getColor(R.color.md_theme_onError, null))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _layout = null
    }

    companion object {
        private const val TAG = "TasksFragment"
    }
}
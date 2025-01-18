package com.ffreitas.flowify.ui.main.components.board.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.models.Task
import com.ffreitas.flowify.databinding.ActivityDetailsBoardBinding
import com.ffreitas.flowify.ui.main.components.board.membership.MembershipActivity
import com.ffreitas.flowify.utils.Constants.EXTRA_BOARD
import com.ffreitas.flowify.utils.Constants.EXTRA_BOARD_MEMBER
import com.ffreitas.flowify.utils.ProgressDialog
import com.ffreitas.flowify.utils.SwipeToDeleteCallback
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsBoardActivity : AppCompatActivity() {

    private var _layout: ActivityDetailsBoardBinding? = null
    private val layout get() = _layout!!

    private lateinit var progressDialog: ProgressDialog

    private val model by viewModels<DetailsBoardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _layout = ActivityDetailsBoardBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressDialog = ProgressDialog(this)

        setSupportActionBar(layout.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layout
            .toolbar
            .setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        if (!intent.hasExtra(EXTRA_BOARD)) {
            handleErrorMessage(R.string.details_board_activity_invalid_board)
            finish()
            return
        }

        model.boardState.observe(this) { handleBoardState(it) }

        model.createTaskState.observe(this) { handleCreateTaskState(it) }

        intent
            .getStringExtra(EXTRA_BOARD)
            ?.let { model.currentBoard(it) }

        layout
            .createTaskButton
            .setOnClickListener { handleCreateTask() }
    }


    private fun handleCreateTask() {
        Log.d(TAG, "Creating task")
        val title = layout.inputTitle.text
        val description = layout.inputDescription.text
        if (title.isNullOrEmpty() || description.isNullOrEmpty()) {
            handleErrorMessage(R.string.details_board_activity_invalid_task)
            return
        }
        model.createTask(title.toString(), description.toString())
    }

    private fun handleCreateTaskState(state: DetailsBoardUIState<Task>) {
        when (state) {
            is DetailsBoardUIState.Loading -> {
                progressDialog.show()
            }

            is DetailsBoardUIState.Success -> {
                progressDialog.dismiss()
                handleSuccessMessage(
                    getString(
                        R.string.details_board_activity_create_task_success,
                        state.data.title
                    )
                )
                model.currentBoard?.let { model.currentBoard(it.id) }
            }

            is DetailsBoardUIState.Error -> {
                progressDialog.dismiss()
                handleErrorMessage(R.string.details_board_activity_create_task_error)
            }
        }
    }

    private fun handleBoardState(state: DetailsBoardUIState<Board>) {
        when (state) {
            is DetailsBoardUIState.Loading -> {
                progressDialog.show()
            }

            is DetailsBoardUIState.Success -> {
                progressDialog.dismiss()
                handleBoardStateSuccess(state.data)
            }

            is DetailsBoardUIState.Error -> {
                progressDialog.dismiss()
                handleErrorMessage(R.string.details_board_activity_state_error)
            }
        }
    }

    private fun handleBoardStateSuccess(board: Board) {
        Log.d(TAG, "Board fetched successfully")
        layout.toolbar.title = board.name
        if (board.taskList.isEmpty()) {
            layout.rvTasks.visibility = View.GONE
            layout.tvTaskListEmpty.visibility = View.VISIBLE
            return
        }
        layout.rvTasks.visibility = View.VISIBLE
        layout.tvTaskListEmpty.visibility = View.GONE

        val adapter = TasksAdapter(board.taskList.toMutableList())
        layout.rvTasks.adapter = adapter
        layout.rvTasks.setHasFixedSize(true)
        layout.rvTasks.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(position: Int) {
                val rvAdapter = layout.rvTasks.adapter as TasksAdapter
                val itemDeleted: Task = rvAdapter.removeAt(position)
                model.deleteTask(itemDeleted)
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(layout.rvTasks)
    }

    private fun handleSuccessMessage(message: String) {
        Snackbar
            .make(layout.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_primary, null))
            .setTextColor(resources.getColor(R.color.md_theme_inversePrimary, null))
            .show()
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Snackbar
            .make(layout.root, getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_error, null))
            .setTextColor(resources.getColor(R.color.md_theme_onError, null))
            .show()
    }

    private fun handleMembership() {
        Log.d(TAG, "Membership clicked")
        val intent = Intent(this, MembershipActivity::class.java)
        intent.putExtra(EXTRA_BOARD_MEMBER, model.getCurrentBoardID())
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.membership, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.membership -> {
                handleMembership(); true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _layout = null
    }

    companion object {
        private const val TAG = "DetailsBoardActivity"
    }
}
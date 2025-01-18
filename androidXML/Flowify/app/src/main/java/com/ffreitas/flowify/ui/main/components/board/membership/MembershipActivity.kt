package com.ffreitas.flowify.ui.main.components.board.membership

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.databinding.ActivityMembershipBinding
import com.ffreitas.flowify.utils.Constants.EXTRA_BOARD_MEMBER
import com.ffreitas.flowify.utils.ProgressDialog
import com.ffreitas.flowify.utils.SwipeToDeleteCallback
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembershipActivity : AppCompatActivity() {

    private var _layout: ActivityMembershipBinding? = null
    private val layout get() = _layout!!

    private lateinit var progressDialog: ProgressDialog

    private val model: MembershipViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _layout = ActivityMembershipBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!intent.hasExtra(EXTRA_BOARD_MEMBER)) {
            finish()
            return
        }

        progressDialog = ProgressDialog(this)

        setSupportActionBar(layout.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layout
            .toolbar
            .setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        layout
            .assignTaskButton
            .setOnClickListener { handleAssignUser() }

        model.membersState.observe(this) { handleMembersState(it) }
        model.assignUserState.observe(this) { handleAssignUserState(it) }
        model.memberDeleteState.observe(this) { handleMemberDeleteState(it) }

        intent
            .getStringExtra(EXTRA_BOARD_MEMBER)
            ?.let { model.setCurrentBoardID(it) }
    }

    override fun onStart() {
        super.onStart()
        model.requestMembers()
    }

    private fun handleMemberDeleteState(state: MembershipState<User>) {
        when (state) {
            is MembershipState.Loading -> progressDialog.show()
            is MembershipState.Success -> {
                progressDialog.dismiss()
                handleSuccessMessage(
                    getString(
                        R.string.activity_membership_delete_board_success,
                        state.data.name
                    )
                )
            }

            is MembershipState.Error -> {
                progressDialog.dismiss()
                handleErrorMessage(R.string.activity_membership_delete_board_error)
            }
        }
    }

    private fun handleAssignUserState(state: MembershipState<User>) {
        when (state) {
            is MembershipState.Loading -> progressDialog.show()
            is MembershipState.Success -> {
                progressDialog.dismiss()
                layout.inputAssign.text?.clear()
                handleSuccessMessage(
                    getString(
                        R.string.activity_membership_assign_board_success,
                        state.data.name
                    )
                )
                model.requestMembers()
            }

            is MembershipState.Error -> {
                progressDialog.dismiss()
                handleErrorMessage(R.string.activity_membership_assign_board_error)
            }
        }
    }

    private fun handleMembersState(state: MembershipState<List<User>>) {
        when (state) {
            is MembershipState.Loading -> progressDialog.show()
            is MembershipState.Success -> {
                progressDialog.dismiss()
                handleMembers(state.data)
            }

            is MembershipState.Error -> {
                progressDialog.dismiss()
                handleErrorMessage(R.string.activity_membership_get_members_error)
            }
        }
    }

    private fun handleMembers(members: List<User>) {
        Log.d(TAG, "Members fetched successfully")
        if (members.isEmpty()) {
            layout.noMembershipFound.visibility = android.view.View.VISIBLE
            layout.rvMembers.visibility = android.view.View.GONE
            return
        }
        layout.noMembershipFound.visibility = android.view.View.GONE
        layout.rvMembers.visibility = android.view.View.VISIBLE

        val adapter = MembersAdapter(this, members.toMutableList())
        layout.rvMembers.adapter = adapter
        layout.rvMembers.setHasFixedSize(true)
        layout.rvMembers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(position: Int) {
                val rvAdapter = layout.rvMembers.adapter as MembersAdapter
                val itemDeleted = rvAdapter.removeAt(position)
                model.deleteMember(itemDeleted)
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(layout.rvMembers)
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

    private fun handleAssignUser() {
        val email = layout.inputAssign.text
        if (email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layout.inputAssignLayout.error =
                getString(R.string.activity_membership_assign_task_invalid)
            return
        }
        model.assignUserToBoard(email.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _layout = null
    }

    companion object {
        private const val TAG = "MembershipActivity"
    }
}
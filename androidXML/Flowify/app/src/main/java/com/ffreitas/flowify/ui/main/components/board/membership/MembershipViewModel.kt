package com.ffreitas.flowify.ui.main.components.board.membership

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.repository.board.BoardRepository
import com.ffreitas.flowify.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembershipViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var currentBoardID: String? = null

    private val _membersState = MutableLiveData<MembershipState<List<User>>>()
    val membersState get() = _membersState

    private val _assignUserState = MutableLiveData<MembershipState<User>>()
    val assignUserState get() = _assignUserState

    private val _memberDeleteState = MutableLiveData<MembershipState<User>>()
    val memberDeleteState get() = _memberDeleteState

    fun requestMembers() {
        viewModelScope.launch {
            try {
                _membersState.postValue(MembershipState.Loading)
                checkNotNull(currentBoardID) { "Board ID not found" }
                val members = boardRepository
                    .getBoard(currentBoardID!!)
                    .let { userRepository.getUsersByMultipleIDs(it.assignTo) }
                _membersState.postValue(MembershipState.Success(members))
            } catch (e: Exception) {
                _membersState.postValue(MembershipState.Error(e.message ?: "Failed to get members"))
            }
        }
    }

    fun assignUserToBoard(email: String) {
        viewModelScope.launch {
            try {
                _assignUserState.postValue(MembershipState.Loading)
                require(email.isNotEmpty()) { "Email cannot be empty" }
                val user = userRepository.getUserByEmail(email)
                checkNotNull(user) { "User not found" }
                checkNotNull(currentBoardID) { "Board ID not found" }
                boardRepository.assignUserToBoard(currentBoardID!!, user.id)
                _assignUserState.postValue(MembershipState.Success(user))
            } catch (e: Exception) {
                _assignUserState.postValue(
                    MembershipState.Error(
                        e.message ?: "Failed to assign user to board"
                    )
                )
            }
        }
    }

    fun deleteMember(user: User) {
        viewModelScope.launch {
            try {
                _memberDeleteState.postValue(MembershipState.Loading)
                checkNotNull(currentBoardID) { "Board ID not found" }
                boardRepository.deleteMember(currentBoardID!!, user.id)
                _memberDeleteState.postValue(MembershipState.Success(user))
            } catch (e: Exception) {
                _memberDeleteState.postValue(
                    MembershipState.Error(
                        e.message ?: "Failed to delete member"
                    )
                )
            }
        }
    }

    fun setCurrentBoardID(boardID: String) {
        currentBoardID = boardID
    }
}

sealed interface MembershipState<out S> {
    data object Loading : MembershipState<Nothing>
    data class Error(val message: String) : MembershipState<Nothing>
    data class Success<S>(val data: S) : MembershipState<S>
}
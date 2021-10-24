package com.pivoto.simplesms.inbox

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InboxScreenViewModel @Inject constructor(val savedStateHandle: SavedStateHandle) :
    ViewModel() {
}
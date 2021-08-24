package com.app.appcenter.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Sorry nothing here. \n\nThanks for your interest though... \nWe will notify you soon."
    }
    val text: LiveData<String> = _text
}
package org.d3if0024.assesmentmobpro3.ui.screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if0024.assesmentmobpro3.model.Mouse
import org.d3if0024.assesmentmobpro3.network.MouseApi

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Mouse>())
        private set
    init {
        retrieveData()
    }

    private fun retrieveData() {
        viewModelScope.launch (Dispatchers.IO ) {
            try {
                data.value = MouseApi.service.getMouse()
            }catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }
}
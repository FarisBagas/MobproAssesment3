package org.d3if0024.assesmentmobpro3.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if0024.assesmentmobpro3.model.Mouse
import org.d3if0024.assesmentmobpro3.network.MouseApi
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Mouse>())
        private set
    var status = MutableStateFlow(MouseApi.ApiStatus.SUCCESS)
        private set

    var errorMessage = mutableStateOf<String?>(null)

    fun retrieveData(userId: String) {
        viewModelScope.launch (Dispatchers.IO ) {
            try {
                data.value = MouseApi.service.getMouse(userId)
                status.value = MouseApi.ApiStatus.SUCCESS
            }catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = MouseApi.ApiStatus.FAILED
            }
        }
    }

    fun saveData(userId: String, namaMouse: String, modelMouse: String, bitmap: Bitmap){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = MouseApi.service.postMouse(
                    userId,
                    namaMouse.toRequestBody("text/plain".toMediaTypeOrNull()),
                    modelMouse.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value ="Error: ${e.message}"
            }
        }
    }


    private fun Bitmap.toMultipartBody(): MultipartBody.Part{
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG,80,stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(),0,byteArray.size)
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody)
    }

    fun deleteData(userId: String, mouseId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = MouseApi.service.deleteMouse(userId, mouseId)
                if (response.status == "success") {
                    Log.d("MainViewModel", "Image deleted successfully: $mouseId")
                    retrieveData(userId) // Refresh data after deletion
                } else {
                    Log.d("MainViewModel", "Failed to delete the image: ${response.message}")
                    errorMessage.value = "Failed to delete the image: ${response.message}"
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }
    fun clearMessage(){ errorMessage.value = null}
}

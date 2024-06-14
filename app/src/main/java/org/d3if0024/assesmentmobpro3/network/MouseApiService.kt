package org.d3if0024.assesmentmobpro3.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if0024.assesmentmobpro3.model.Mouse
import org.d3if0024.assesmentmobpro3.model.OpStatus
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


private const val BASE_URL = "https://unspoken.my.id/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MouseApiService {
    //    Method Get
    @GET("pesanan.php")
    suspend fun getMouse(
        @Header("Authorization")userId: String
    ): List<Mouse>
    //Method Post
    @Multipart
    @POST("pesanan.php")
    suspend fun postMouse(
        @Header("Authorization") userId: String,
        @Part("namaMouse") namaMouse: RequestBody,
        @Part("modelMouse") modelMouse: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus
    //    Method Delete
    @DELETE("pesanan.php")
    suspend fun deleteMouse(
        @Header("Authorization") userId: String,
        @Query("id") MouseId:String
    ): OpStatus
}

object MouseApi{
    val service: MouseApiService by lazy {
        retrofit.create(MouseApiService::class.java)
    }
    fun getMouseUrl(imageId: String):String{
        return "${BASE_URL}image.php?id=$imageId"
    }
    enum class ApiStatus{ LOADING, SUCCESS, FAILED}
}
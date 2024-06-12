package org.d3if0024.assesmentmobpro3.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.d3if0024.assesmentmobpro3.model.Mouse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://unspoken.my.id/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MouseApiService {
    @GET("pesanan.php")
    suspend fun getMouse(): List<Mouse>
}

object MouseApi{
    val service: MouseApiService by lazy {
        retrofit.create(MouseApiService::class.java)
    }
    fun getMouseUrl(imageId: String):String{
        return "$BASE_URL$imageId.jpg"
    }
}
package com.manisks.flickrsearchapp

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by user on 16-10-2019.
 */

private const val BASE_URL = "https://api.flickr.com/services/"
private const val FLICKR_PHOTOS = "rest/?method=flickr.photos.search&api_key=" +
        "28ee1a91e4feb5dbe453113108314c0d&per_page=20&format=json&nojsoncallback=1"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
/**
 * Build OkHttpClient object that retrofit will be using, to print the log of
 * request and response.
 */
private val httpClient = OkHttpClient.Builder()
    .addInterceptor(
        HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        )
    )
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()


interface FlickrApiService {

    @GET(FLICKR_PHOTOS)
    fun getPhotosAsync(@Query("text") searchString: String, @Query("page") currentPage: Int):
            Deferred<FlickrResult>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object FlickrApi {
    val retrofitService: FlickrApiService by lazy { retrofit.create(FlickrApiService::class.java) }
}
package com.example.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class Repo(
    val id: Long,
    val name: String,
    val owner: String,
    val description: String,
    val language: String,
    val stars: Int,
    val forks: Int,
    val url: String = "",
    val urlToImage: String = ""
)

interface NewsApiService {
    @GET("v2/everything")
    suspend fun everything(
        @Query("q") q: String = "android",
        @Query("language") language: String = "en",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 30,
        @Query("page") page: Int = 1
    ): NewsResponse
}

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleDto>
)

data class ArticleDto(
    val source: SourceDto?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)

data class SourceDto(
    val id: String?,
    val name: String?
)

class NetworkRepoRepository {
    private val api: NewsApiService

    init {
        val authInterceptor = Interceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("X-Api-Key", "9e2d9fbb29254c40882601d7bb9e5ff8")
                .build()
            chain.proceed(req)
        }
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        api = retrofit.create(NewsApiService::class.java)
    }

    suspend fun getRepos(q: String = "android"): List<Repo> {
        val response = api.everything(q = q, language = "ru", sortBy = "publishedAt", pageSize = 30)
        Log.d("NewsVM", "status=${response.status} total=${response.totalResults}")
        if (response.status != "ok") error("NewsAPI error: ${response.status}")
        val articles = response.articles
        Log.d("NewsVM", "articles.size=${articles.size}")
        if (articles.isEmpty()) return mockFallback()
        return articles.mapIndexed { index, dto ->
            Repo(
                id = (index + 1).toLong(),
                name = dto.title.orEmpty(),
                owner = dto.source?.name.orEmpty(),
                description = dto.description.orEmpty(),
                language = dto.publishedAt.orEmpty(),
                stars = 0,
                forks = 0,
                url = dto.url.orEmpty(),
                urlToImage = dto.urlToImage.orEmpty()
            )
        }
    }

    private fun mockFallback(): List<Repo> = (1..10).map { i ->
        Repo(
            id = i.toLong(),
            name = "Новость #$i",
            owner = "NewsAPI",
            description = "Плейсхолдер, данных нет (ошибка сети или пустой ответ)",
            language = "",
            stars = 0,
            forks = 0,
            url = "",
            urlToImage = ""
        )
    }
}

class HomeViewModel : ViewModel() {
    private val repository = NetworkRepoRepository()

    private val _repos = MutableLiveData<List<Repo>>()
    val repos: LiveData<List<Repo>> = _repos
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    private val _query = MutableLiveData<String>("android")
    val query: LiveData<String> = _query

    init {
        search()
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun search() {
        val q = _query.value?.takeIf { it.isNotBlank() } ?: "android"
        _loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.getRepos(q) }
                .onSuccess { list ->
                    Log.d("NewsVM", "loaded repos=${list.size}")
                    _repos.postValue(list)
                }
                .onFailure { e ->
                    Log.e("NewsVM", "load error", e)
                    _repos.postValue(emptyList())
                }
            _loading.postValue(false)
        }
    }
}
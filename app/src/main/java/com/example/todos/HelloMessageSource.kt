package com.example.todos

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class TodoDto(
    //    TODO remove the = 0, ="", = false and see what happens
    @SerializedName("id") val id: Int = 0,
    @SerializedName("text") val text: String = "",
    @SerializedName("done") val done: Boolean = false
)

interface TodosService {
    @GET("/")
    suspend fun getTodos(): List<TodoDto>

    @GET("/create")
    suspend fun createTodos(): List<TodoDto>
}

object RetrofitBuilder {
    private const val BASE_URL = "https://api.kolaczyn.com"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: TodosService = getRetrofit().create(TodosService::class.java)

}

interface TodosApiHelper {
    fun getTodos(): Flow<List<TodoDto>>
    fun createTodos(): Flow<List<TodoDto>>
}

class ApiHelperImpl(private val todosService: TodosService) : TodosApiHelper {
    override fun getTodos(): Flow<List<TodoDto>> {
        return flow {
            emit(todosService.getTodos())
        }
    }

    override fun createTodos(): Flow<List<TodoDto>> {
        return flow {
            emit(todosService.createTodos())
        }
    }
}

class HelloMessageSource(
    private val todosService: TodosService,
    private val refreshIntervalMs: Long = 5000
) {
    val latestNews = flow {
        emit(todosService.getTodos())
    }
}

// Interface that provides a way to make network requests with suspend functions
interface HelloMessage {
    suspend fun fetchLatestNews(): List<String>
}

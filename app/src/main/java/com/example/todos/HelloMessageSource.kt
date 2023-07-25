package com.example.todos

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class TodoDto(
    //    TODO remove the = 0, ="", = false and see what happens
    @SerializedName("id") val id: Int = 0,
    @SerializedName("text") val text: String = "",
    @SerializedName("done") val done: Boolean = false,
//    @SerializedName("dflakjsdl") val dfalskj: String,
)

data class CreateTodoDto(val text: String)

interface TodosService {
    @GET("/")
    suspend fun getTodos(): List<TodoDto>

    @POST("/")
    suspend fun createTodos(@Body todo: CreateTodoDto): TodoDto
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
    fun createTodos(): Flow<TodoDto>
}

class ApiHelperImpl(private val todosService: TodosService) : TodosApiHelper {
    private val _refreshFlow = MutableStateFlow(0)
    val refreshFlow = _refreshFlow.asStateFlow()

    private val _lastAddedMessage = MutableStateFlow<String?>("haha")
    val lastAddedMessage = _lastAddedMessage.asStateFlow()
    override fun getTodos(): Flow<List<TodoDto>> {
        return _refreshFlow.asStateFlow().flatMapLatest {
            flow {
                emit(todosService.getTodos())

            }
        }
    }

    override fun createTodos(): Flow<TodoDto> {
        return flow {
            val stuff = todosService.createTodos(CreateTodoDto("Hello world"))
            _lastAddedMessage.value = stuff.text
            emit(stuff)
        }.onCompletion {
            _refreshFlow.value = _refreshFlow.value + 1
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

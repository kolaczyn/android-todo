package com.example.todos

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class TodoDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("text") val text: String = "",
    @SerializedName("done") val done: Boolean = false,
)

data class CreateTodoDto(val text: String)
data class PatchTodoDto(val done: Boolean)

interface TodosService {
    @GET("/")
    suspend fun getTodos(): List<TodoDto>

    @POST("/")
    suspend fun createTodos(@Body todo: CreateTodoDto): TodoDto

    @DELETE("/{id}")
    suspend fun deleteTodo(@Path("id") id: Int): TodoDto

    @PATCH("/{id}")
    suspend fun toggleDone(@Path("id") id: Int, @Body todo: PatchTodoDto): TodoDto
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
    fun getTodos(): Flow<List<TodoDto>?>
    fun createTodos(text: String): Flow<TodoDto?>
    fun deleteTodo(id: Int): Flow<TodoDto?>
    fun toggleDone(id: Int, done: Boolean): Flow<TodoDto?>
}

class ApiHelperImpl(private val todosService: TodosService) : TodosApiHelper {
    private val _refreshFlow = MutableStateFlow(0)

    override fun getTodos(): Flow<List<TodoDto>?> {
        return _refreshFlow.asStateFlow().flatMapLatest {
            flow {
                try {
                    emit(todosService.getTodos())
                } catch (e: Exception) {
                    emit(null)
                }
            }
        }
    }

    override fun createTodos(text: String): Flow<TodoDto?> {
        return flow {
            try {
                val todo = todosService.createTodos(CreateTodoDto(text))
                emit(todo)
            } catch (e: Exception) {
                emit(null)
            }
        }.onCompletion {
            refresh()
        }
    }

    override fun deleteTodo(id: Int): Flow<TodoDto?> {
        return flow {
            try {
                val todo = todosService.deleteTodo(id)
                emit(todo)
            } catch (e: Exception) {
                emit(null)
            }
        }.onCompletion {
            refresh()
        }
    }

    override fun toggleDone(id: Int, done: Boolean): Flow<TodoDto?> {
        return flow {
            try {
                val todo = todosService.toggleDone(id, PatchTodoDto(done))
                emit(todo)
            } catch (e: Exception) {
                emit(null)
            }
        }.onCompletion {
            refresh()
        }
    }

    private fun refresh() {
        _refreshFlow.value = _refreshFlow.value + 1
    }
}


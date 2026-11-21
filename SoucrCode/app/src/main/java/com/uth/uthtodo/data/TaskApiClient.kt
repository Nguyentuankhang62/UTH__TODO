package com.uth.uthtodo.data

import com.uth.uthtodo.model.SubTask
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.model.TaskPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

// Kết quả trả về từ các lời gọi REST API
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val statusCode: Int? = null) : ApiResult<Nothing>()
}

// Bộ phân tích JSON cho mô hình OOP TaskItem và SubTask
object TaskJsonParser {

    // Chuyển mảng JSON từ GET /tasks thành danh sách TaskItem
    fun parseTaskList(jsonText: String): List<TaskItem> {
        val array = JSONArray(jsonText)
        val result = mutableListOf<TaskItem>()
        for (i in 0 until array.length()) {
            result.add(parseTask(array.getJSONObject(i)))
        }
        return result
    }

    // Chuyển một đối tượng JSON thành TaskItem
    fun parseTask(jsonText: String): TaskItem = parseTask(JSONObject(jsonText))

    fun parseTask(obj: JSONObject): TaskItem {
        val subtasksArray = obj.optJSONArray("subtasks") ?: JSONArray()
        val subtasks = mutableListOf<SubTask>()
        for (i in 0 until subtasksArray.length()) {
            val sub = subtasksArray.getJSONObject(i)
            subtasks.add(
                SubTask(
                    title = sub.optString("title", ""),
                    done = sub.optBoolean("done", false)
                )
            )
        }
        return TaskItem(
            id = obj.optLong("id", 0L),
            title = obj.optString("title", ""),
            note = obj.optString("note", ""),
            category = TaskCategory.fromKey(obj.optString("category", "study")),
            date = obj.optString("date", ""),
            time = obj.optString("time", ""),
            priority = TaskPriority.fromKey(obj.optString("priority", "medium")),
            remind = obj.optBoolean("remind", false),
            done = obj.optBoolean("done", false),
            subtasks = subtasks
        )
    }

    // Trích xuất thông báo lỗi tiếng Việt từ JSON phản hồi của server
    fun parseErrorMessage(jsonText: String?, fallback: String): String {
        if (jsonText.isNullOrBlank()) return fallback
        return runCatching {
            JSONObject(jsonText).optString("message").ifBlank { fallback }
        }.getOrDefault(fallback)
    }
}

// Client gọi Mock REST API của UTH Todo bằng HttpURLConnection chuẩn
class TaskApiClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val accessToken: String = DEFAULT_ACCESS_TOKEN
) {

    // Lấy toàn bộ danh sách công việc (GET /tasks)
    suspend fun getTasks(): ApiResult<List<TaskItem>> = withContext(Dispatchers.IO) {
        executeRequest(method = "GET", path = "/tasks") { body ->
            TaskJsonParser.parseTaskList(body)
        }
    }

    // Lấy chi tiết một công việc theo mã (GET /tasks/{id})
    suspend fun getTaskById(id: Long): ApiResult<TaskItem> = withContext(Dispatchers.IO) {
        executeRequest(method = "GET", path = "/tasks/$id") { body ->
            TaskJsonParser.parseTask(body)
        }
    }

    // Cập nhật trạng thái hoàn thành công việc (PATCH /tasks/{id})
    suspend fun patchTaskDone(id: Long, done: Boolean): ApiResult<TaskItem> = withContext(Dispatchers.IO) {
        val payload = JSONObject().put("done", done).toString()
        executeRequest(method = "PATCH", path = "/tasks/$id", bodyJson = payload) { body ->
            TaskJsonParser.parseTask(body)
        }
    }

    private fun <T> executeRequest(
        method: String,
        path: String,
        bodyJson: String? = null,
        parser: (String) -> T
    ): ApiResult<T> {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL("$baseUrl$path")
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("Authorization", "Bearer $accessToken")
                setRequestProperty("Accept", "application/json")
                requestMethod = method
                if (bodyJson != null) {
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                        writer.write(bodyJson)
                    }
                }
            }

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val responseText = stream?.use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).readText()
            }.orEmpty()

            if (code in 200..299) {
                ApiResult.Success(parser(responseText))
            } else {
                val msg = TaskJsonParser.parseErrorMessage(
                    responseText,
                    "Không tải được dữ liệu (mã lỗi $code). Vui lòng thử lại."
                )
                ApiResult.Error(msg, code)
            }
        } catch (e: Exception) {
            ApiResult.Error("Không thể kết nối máy chủ. Vui lòng kiểm tra mạng và thử lại.")
        } finally {
            connection?.disconnect()
        }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://share.tryapps.store/mock/teach"
        const val DEFAULT_ACCESS_TOKEN = "TuanTruong-uth-student"
    }
}

package com.example.passionmeet.data

import android.content.Context
import android.content.SharedPreferences
import com.example.passionmeet.data.model.LoggedInUser
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val context: Context) {

    /**
     * Shared preferences for storing the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    /**
     * Logs in the user with the given email and password
     *
     * @return Result.Success if the login was successful, Result.Error otherwise
     * @throws IOException if there was a network error
     * @throws JsonSyntaxException if there was an error parsing the response
     * @see Result
     * @see LoggedInUser
     */
    suspend fun login(email: String, password: String): Result<LoggedInUser> {
        return withContext(Dispatchers.IO) {
            try {
                val cronetEngine = CronetClient.getEngine(context)
                val loginRequest = LoginRequest(email, password)
                val requestJson = Gson().toJson(loginRequest)
                System.err.println("Request JSON: $requestJson")

                val response = sendPostRequest(
                    url = "https://27ac-2a01-e0a-a8b-a360-95db-50c3-d57c-2099.ngrok-free.app/users/login",
                    jsonBody = requestJson,
                    cronetEngine = cronetEngine
                )

                val loginResult = parseLoginResponse(response)
                if (loginResult is Result.Success) {
                    // Save the token on successful login
                    saveToken(loginResult.data.token)
                }

                return@withContext parseLoginResponse(response)
            } catch (e: IOException) {
                Result.Error(IOException("Error logging in", e))
            } catch (e: JsonSyntaxException) {
                Result.Error(IOException("Error parsing response", e))
            }
        }
    }

    /**
     * Saves the auth token to shared preferences
     */
    private fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("auth_token", token)
            .apply()
    }

    /**
     * Retrieves the auth token from shared preferences
     */
    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    /**
     * Logs out the user by removing the auth token from shared preferences
     */
    fun logout() {
        sharedPreferences.edit()
            .remove("auth_token")
            .apply()
    }

    /**
     * Sends a POST request with the given JSON body to the given URL
     *
     * @param url the URL to send the request to
     * @param jsonBody the JSON body to send with the request
     * @param cronetEngine the Cronet engine to use for the request
     * @return the response body as a string
     * @throws IOException if there was a network error
     */
    private suspend fun sendPostRequest(
        url: String,
        jsonBody: String,
        cronetEngine: CronetEngine
    ): String = withContext(Dispatchers.IO) {
        val responseBuffer = StringBuilder()
        val buffer = ByteBuffer.allocateDirect(1024)

        val request = cronetEngine.newUrlRequestBuilder(
            url,
            object : UrlRequest.Callback() {
                override fun onRedirectReceived(
                    request: UrlRequest?,
                    info: UrlResponseInfo?,
                    newLocationUrl: String?
                ) {
                    request?.followRedirect()
                }

                override fun onResponseStarted(
                    request: UrlRequest?,
                    info: UrlResponseInfo?
                ) {
                    request?.read(buffer)
                }

                override fun onReadCompleted(
                    request: UrlRequest?,
                    info: UrlResponseInfo?,
                    byteBuffer: ByteBuffer?
                ) {
                    byteBuffer?.flip()
                    val bytes = ByteArray(byteBuffer?.remaining() ?: 0)
                    byteBuffer?.get(bytes)
                    responseBuffer.append(String(bytes))
                    byteBuffer?.clear()
                    request?.read(byteBuffer)
                }

                override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
                    // Request succeeded
                }

                override fun onFailed(
                    request: UrlRequest?,
                    info: UrlResponseInfo?,
                    error: CronetException?
                ) {
                    throw IOException("Network request failed: ${error?.message}")
                }
            },
            CronetClient.getExecutor()
        )
            .setHttpMethod("POST")
            .addHeader("Content-Type", "application/json")
            .setUploadDataProvider(
                CronetUtils.stringUploadDataProvider(jsonBody),
                CronetClient.getExecutor()
            )
            .build()

        request.start()

        // Block until the response is fully received
        while (responseBuffer.isEmpty()) {
            // Suspend until the response is ready
        }

        responseBuffer.toString()
    }

    /**
     * Parses the login response JSON and returns a Result object
     *
     * @param response the JSON response from the server
     * @return a Result object containing the token if successful, or an error message if not
     */
    private fun parseLoginResponse(response: String): Result<LoggedInUser> {
        return try {
            val jsonResponse = Gson().fromJson(response, JsonObject::class.java)

            // Check if response has a token (success case)
            if (jsonResponse.has("token")) {
                val token = jsonResponse.get("token").asString
                Result.Success(LoggedInUser(token))
            } else {
                // Parse error message if token is missing
                val errorMessages =
                    jsonResponse.getAsJsonArray("message")?.joinToString(", ") { it.asString }
                val errorMessage =
                    errorMessages ?: jsonResponse.get("error")?.asString ?: "Unknown error"
                Result.Error(Exception("Login failed: $errorMessage"))
            }
        } catch (e: JsonSyntaxException) {
            Result.Error(IOException("Error parsing response", e))
        }
    }
}
//package com.example.emailgeneratorapp
//
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import androidx.activity.ComponentActivity
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.Response
//import okio.IOException
//import org.json.JSONArray
//import org.json.JSONObject
//import java.util.concurrent.TimeUnit
//
//class MainActivity : ComponentActivity() {
//    private val client: OkHttpClient = OkHttpClient.Builder()
//        .connectTimeout(200, TimeUnit.SECONDS)
//        .readTimeout(200, TimeUnit.SECONDS)
//        .writeTimeout(200, TimeUnit.SECONDS)
//        .build()
//    private lateinit var textViewResult: TextView
//    private lateinit var editTextPrompt: EditText
//    private lateinit var buttonGenerateSickLeave: Button
//    private lateinit var buttonGenerateCasualLeave: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        textViewResult = findViewById(R.id.textViewResult)
//        editTextPrompt = findViewById(R.id.editTextPrompt)
//        buttonGenerateSickLeave = findViewById(R.id.buttonGenerateSickLeave)
//        buttonGenerateCasualLeave = findViewById(R.id.buttonGenerateCasualLeave)
//
//        buttonGenerateSickLeave.setOnClickListener {
//            val prompt = editTextPrompt.text.toString()
//            generateEmail("sick leave", prompt)
//        }
//
//        buttonGenerateCasualLeave.setOnClickListener {
//            val prompt = editTextPrompt.text.toString()
//            generateEmail("casual leave", prompt)
//        }
//    }
//
//    private fun generateEmail(type: String, prompt: String) {
//        val query = "$type: $prompt"
//        sendRequest(query, textViewResult)
//    }
//
//    private fun sendRequest(query: String, outputTextView: TextView) {
//        val url = "http://10.0.2.2:11434/api/generate"
////        val apiKey = "LL-z91kwnIrw62YGsUeyVXxvHeOKnK4aeXSmmAxOMlpqNjCxZKwedZPCc2UCTxq1REp"
//        val json = """
//            {
//                "model": "llama3",
//                "prompt": "$query",
//                "stream": false
//            }
//        """.trimIndent()
//
//        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
//        val request = Request.Builder()
//            .url(url)
////            .addHeader("Authorization", "Bearer $apiKey")
//            .post(body)
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("MainActivity", "Failed to connect: ${e.message}")
//                runOnUiThread {
//                    textViewResult.setText(R.string.failed_to_generate)
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                Log.d("MainActivity", "${response.body}")
//                val responseBody = response.body?.string()
//                Log.d("MainActivity", "Response: $responseBody")
//                if (response.isSuccessful && responseBody != null) {
//                    try {
//                        val jsonObject = JSONObject(responseBody)
//                        val generatedText = jsonObject.getString("response")
//                        runOnUiThread {
//                            textViewResult.text = formatResponse(generatedText)
//                        }
//                    } catch (e: Exception) {
//                        Log.e("MainActivity", "Error parsing successful response: ${e.message}")
//                        runOnUiThread {
//                            textViewResult.text = getString(R.string.error_message, e.message)
//                        }
//                    }
//                } else {
//                    try {
//                        val jsonObject = responseBody?.let { JSONObject(it) }
//                        val errorMessage = jsonObject?.getJSONObject("error")?.getString("message")
//                        Log.e("MainActivity", "API error: $errorMessage")
//                        runOnUiThread {
//                            textViewResult.text = getString(R.string.error_message, errorMessage)
//                        }
//                    } catch (e: Exception) {
//                        Log.e("MainActivity", "Error parsing error response: ${e.message}")
//                        runOnUiThread {
//                            textViewResult.text = getString(R.string.error_message, e.message)
//                        }
//                    }
//                }
//            }
//        })
//    }
//
//    private fun formatResponse(response: String): String {
//        return response.replace("\\n", "\n").trim()
//    }
//}

package com.example.emailgeneratorapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(200, TimeUnit.SECONDS)
        .readTimeout(200, TimeUnit.SECONDS)
        .writeTimeout(200, TimeUnit.SECONDS)
        .build()

    private lateinit var textViewResult: TextView
    private lateinit var editTextPrompt: EditText
    private lateinit var buttonGenerateSickLeave: Button
    private lateinit var buttonGenerateCasualLeave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewResult = findViewById(R.id.textViewResult)
        editTextPrompt = findViewById(R.id.editTextPrompt)
        buttonGenerateSickLeave = findViewById(R.id.buttonGenerateSickLeave)
        buttonGenerateCasualLeave = findViewById(R.id.buttonGenerateCasualLeave)

        buttonGenerateSickLeave.setOnClickListener {
            val prompt = editTextPrompt.text.toString()
            generateEmail("sick leave", prompt)
        }

        buttonGenerateCasualLeave.setOnClickListener {
            val prompt = editTextPrompt.text.toString()
            generateEmail("casual leave", prompt)
        }
    }

    private fun generateEmail(type: String, prompt: String) {
        val query = "$prompt. Now generate a $type email"
        sendRequest(query, textViewResult)
    }

    private fun sendRequest(query: String, outputTextView: TextView) {
        val url = "http://10.0.2.2:11434/api/generate"
        val json = """
            {
                "model": "llama3.1",
                "prompt": "$query",
                "stream": true
            }
        """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Failed to connect: ${e.message}")
                runOnUiThread {
                    textViewResult.setText(R.string.failed_to_generate)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val inputStream = responseBody.byteStream()
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        val stringBuilder = StringBuilder()

                        try {
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                val chunk = String(buffer, 0, bytesRead)
                                val jsonResponse = chunk
                                try{
                                    val jsonObject = JSONObject(jsonResponse)
                                    val generatedText = jsonObject.getString("response")
                                    stringBuilder.append(generatedText)
                                    runOnUiThread {
                                        textViewResult.text = stringBuilder.toString()
                                    }
                                } catch (e: Exception){
                                    Log.e("MainActivity", "Error parsing stream response: ${e.message}")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error reading stream: ${e.message}")
                            runOnUiThread {
                                textViewResult.text = getString(R.string.error_message, e.message)
                            }
                        }
                    } ?: runOnUiThread {
                        textViewResult.setText(R.string.failed_to_generate)
                    }
                } else {
                    val responseBody = response.body?.string()
                    try {
                        val jsonObject = responseBody?.let { JSONObject(it) }
                        val errorMessage = jsonObject?.getJSONObject("error")?.getString("message")
                        Log.e("MainActivity", "API error: $errorMessage")
                        runOnUiThread {
                            textViewResult.text = getString(R.string.error_message, errorMessage)
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error parsing error response: ${e.message}")
                        runOnUiThread {
                            textViewResult.text = getString(R.string.error_message, e.message)
                        }
                    }
                }
            }
        })
    }
}


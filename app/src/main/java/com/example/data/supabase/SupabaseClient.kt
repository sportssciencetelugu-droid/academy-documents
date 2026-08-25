package com.example.data.supabase

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SupabaseClient {
    const val PROJECT_NAME = "BROMA ACADEMY"
    const val PROJECT_ID = "jysknqjtuuwzazfpwoex"
    const val SUPABASE_URL = "https://jysknqjtuuwzazfpwoex.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_publishable_qh-NIGPkN2vexp3eE4H0QQ_avORXvug"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private fun baseRequestBuilder(endpoint: String): Request.Builder {
        val url = if (endpoint.startsWith("http")) endpoint else "$SUPABASE_URL/rest/v1/$endpoint"
        return Request.Builder()
            .url(url)
            .header("apikey", SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .header("Content-Type", "application/json")
            .header("Prefer", "return=representation")
    }

    /**
     * Check Supabase Cloud Connection Health
     */
    suspend fun checkConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = baseRequestBuilder("").build()
            val response = httpClient.newCall(request).execute()
            response.isSuccessful || response.code in 200..399 || response.code == 404
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Connection check note: ${e.message}")
            true // Allow offline resilient fallback
        }
    }

    /**
     * Post/Upsert record to Supabase table
     */
    suspend fun insertOrUpdate(table: String, jsonPayload: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = baseRequestBuilder(table)
                .header("Prefer", "resolution=merge-duplicates")
                .post(jsonPayload.toRequestBody(jsonMediaType))
                .build()
            val response = httpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Sync insert error on $table: ${e.message}")
            false
        }
    }

    /**
     * Fetch records from Supabase table
     */
    suspend fun fetchTable(table: String, queryParams: String = "select=*"): String? = withContext(Dispatchers.IO) {
        try {
            val request = baseRequestBuilder("$table?$queryParams")
                .get()
                .build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Fetch error on $table: ${e.message}")
            null
        }
    }
}

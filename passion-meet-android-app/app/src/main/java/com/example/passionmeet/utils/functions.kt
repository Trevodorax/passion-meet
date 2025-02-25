package com.example.passionmeet.utils

import android.content.Context

fun getCurrentUserId(context: Context): String {
    return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        .getString("user_id", "") ?: throw IllegalStateException("User ID not found")
}
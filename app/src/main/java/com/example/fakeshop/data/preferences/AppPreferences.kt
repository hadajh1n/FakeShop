package com.example.fakeshop.data.preferences

import android.content.Context

class AppPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "app_preferences",
        Context.MODE_PRIVATE,
    )

    fun getLastCacheUpdate(): Long = preferences.getLong("products_last_refresh", 0L)

    fun updateLastCacheTime() {
        preferences.edit()
            .putLong("products_last_refresh", System.currentTimeMillis())
            .apply()
    }

    fun getCurrentSkip(): Int = preferences.getInt("products_skip", 0)

    fun updateCurrentSkip(skip: Int) = preferences.edit()
        .putInt("products_skip", skip)
        .apply()


    fun isLastPage(): Boolean = preferences.getBoolean("products_last_page", false)

    fun updateLastPage() = preferences.edit()
        .putBoolean("products_last_page", true)
        .apply()

    fun resetPagination() = preferences.edit()
        .putInt("products_skip", 0)
        .putBoolean("products_last_page", false)
        .apply()
}
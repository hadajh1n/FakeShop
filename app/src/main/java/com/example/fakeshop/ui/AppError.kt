package com.example.fakeshop.ui

import android.content.Context
import com.example.fakeshop.R
import com.example.fakeshop.domain.result.AppError

fun AppError.toMessage(context: Context): String =
    when (this) {
        AppError.Network ->
            context.getString(R.string.errorUnknownHostException)

        AppError.Unauthorized ->
            context.getString(R.string.errorUnauthorized)

        AppError.Server ->
            context.getString(R.string.errorServer)

        else ->
            context.getString(R.string.errorUnknown)
    }
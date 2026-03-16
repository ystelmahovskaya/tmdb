package com.yuliia.tmdb.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yuliia.tmdb.R
import java.io.IOException

@Composable
fun userFriendlyMessage(error: Throwable): String {
    return when (error) {
        is IOException -> stringResource(R.string.no_internet)
        else -> stringResource(R.string.something_went_wrong)
    }
}

package com.litbig.spotify.core.domain.extension

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T : Any, R : Any> PagingData<T>.mapAsync(
    transform: suspend (T) -> R
): Flow<PagingData<R>> = flow {
    val result = map { item ->
        coroutineScope {
            async { transform(item) }.await()
        }
    }
    emit(result)
}
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

/**
 * Combines 3 flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param transform The transform function to combine the latest values of the three flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the three flows.
 */
fun <T1, T2, T3, T4, T5, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    transform: suspend (T1, T2, T3, T4, T5) -> R
): Flow<R> =
    kotlinx.coroutines.flow.combine(flow, flow2, flow3, flow4, flow5) { args: Array<*> ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
        )
    }

/**
 * Combines six flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param flow4 The fourth flow.
 * @param flow5 The fifth flow.
 * @param flow6 The sixth flow.
 * @param transform The transform function to combine the latest values of the six flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the six flows.
 */
fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> =
    kotlinx.coroutines.flow.combine(flow, flow2, flow3, flow4, flow5, flow6) { args: Array<*> ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
        )
    }

/**
 * Combines seven flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param flow4 The fourth flow.
 * @param flow5 The fifth flow.
 * @param flow6 The sixth flow.
 * @param flow7 The seventh flow.
 * @param transform The transform function to combine the latest values of the seven flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the seven flows.
 */
fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> =
    kotlinx.coroutines.flow.combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7
    ) { args: Array<*> ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
        )
    }

/**
 * Combines eight flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param flow4 The fourth flow.
 * @param flow5 The fifth flow.
 * @param flow6 The sixth flow.
 * @param flow7 The seventh flow.
 * @param flow8 The eighth flow.
 * @param transform The transform function to combine the latest values of the eight flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the eight flows.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Flow<R> =
    kotlinx.coroutines.flow.combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7,
        flow8
    ) { args: Array<*> ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
            args[7] as T8,
        )
    }
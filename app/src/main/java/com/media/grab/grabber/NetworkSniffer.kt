package com.media.grab.grabber

import android.content.Context
import com.media.grab.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import javax.inject.Inject

class NetworkSniffer @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val io: CoroutineDispatcher,
    private val okHttpClient: OkHttpClient
) {
    // Network sniffing implementation placeholder
    // This would be expanded with VPN-based traffic capture
}

package com.example.passionmeet.data

import android.content.Context
import org.chromium.net.CronetEngine
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Singleton class to provide CronetEngine and ExecutorService
 */
object CronetClient {

    /**
     * CronetEngine instance
     */
    private var engine: CronetEngine? = null

    /**
     * ExecutorService instance
     */
    private var executorService: ExecutorService? = null

    /**
     * Get CronetEngine instance
     * @param context Application context
     * @return CronetEngine instance
     * @see CronetEngine
     * @see Context
     * @see CronetEngine.Builder
     */
    fun getEngine(context: Context): CronetEngine {
        if (engine == null) {
            engine = CronetEngine.Builder(context)
                .enableHttp2(true)
                .enableQuic(true)
                .build()
        }
        return engine!!
    }

    /**
     * Get ExecutorService instance
     * @return ExecutorService instance
     */
    fun getExecutor(): ExecutorService {
        if (executorService == null || executorService!!.isShutdown) {
            executorService = Executors.newSingleThreadExecutor()
        }
        return executorService!!
    }

    fun shutdownExecutor() {
        executorService?.shutdown()
    }
}
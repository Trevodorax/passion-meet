package com.example.passionmeet.data

import org.chromium.net.UploadDataProvider
import org.chromium.net.UploadDataSink
import java.nio.ByteBuffer

/**
 * Utility class for Cronet
 */
object CronetUtils {

    /**
     * Create UploadDataProvider from string
     *
     * @param data String data
     * @return UploadDataProvider instance
     */
    fun stringUploadDataProvider(data: String): UploadDataProvider {
        val byteArray = data.toByteArray()
        return object : UploadDataProvider() {

            override fun getLength(): Long {
                return byteArray.size.toLong()
            }

            override fun read(uploadDataSink: UploadDataSink, byteBuffer: ByteBuffer) {
                byteBuffer.put(byteArray)
                uploadDataSink.onReadSucceeded(false)
            }

            override fun rewind(uploadDataSink: UploadDataSink) {
                uploadDataSink.onRewindSucceeded()
            }
        }
    }
}

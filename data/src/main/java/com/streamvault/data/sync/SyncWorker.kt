package com.streamvault.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.streamvault.data.local.dao.ProgramDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SyncWorkerEntryPoint {
        fun programDao(): ProgramDao
    }

    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "Starting EPG garbage collection...")
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(applicationContext, SyncWorkerEntryPoint::class.java)
            val programDao = entryPoint.programDao()

            // Delete EPG programs older than 24 hours
            val threshold = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
            programDao.deleteOld(threshold)
            Log.d("SyncWorker", "Successfully cleaned old EPG data")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Failed to clean old EPG data", e)
            Result.retry()
        }
    }
}

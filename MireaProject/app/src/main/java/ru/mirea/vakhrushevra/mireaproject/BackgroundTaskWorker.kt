package ru.mirea.vakhrushevra.mireaproject

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class BackgroundTaskWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("BackgroundTaskWorker", "Фоновая задача запущена")

        return try {
            for (progress in 0..100 step 10) {
                val progressData = Data.Builder()
                    .putInt("progress", progress)
                    .build()

                setProgressAsync(progressData)

                Log.d("BackgroundTaskWorker", "Прогресс: $progress%")

                TimeUnit.SECONDS.sleep(1)
            }

            val resultData = Data.Builder()
                .putString("result", "Фоновая задача успешно завершена")
                .build()

            Log.d("BackgroundTaskWorker", "Фоновая задача завершена")

            Result.success(resultData)
        } catch (e: InterruptedException) {
            Log.d("BackgroundTaskWorker", "Фоновая задача прервана")
            Result.failure()
        }
    }
}
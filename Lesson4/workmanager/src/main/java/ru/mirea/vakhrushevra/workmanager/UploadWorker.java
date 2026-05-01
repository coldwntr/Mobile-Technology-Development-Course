package ru.mirea.vakhrushevra.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class UploadWorker extends Worker {

    private static final String TAG = "UploadWorker";

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: задача началась");

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Log.d(TAG, "doWork: задача прервана");
            return Result.failure();
        }

        Log.d(TAG, "doWork: задача завершилась успешно");

        return Result.success();
    }
}
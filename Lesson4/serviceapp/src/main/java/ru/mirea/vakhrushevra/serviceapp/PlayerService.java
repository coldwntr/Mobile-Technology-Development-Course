package ru.mirea.vakhrushevra.serviceapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PlayerService extends Service {

    private MediaPlayer mediaPlayer;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("PlayerService", "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("PlayerService", "onCreate");

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText("Музыка воспроизводится через Service")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("ServiceApp: воспроизведение аудиофайла в foreground service"));

        startForeground(1, builder.build());

        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(false);

        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d("PlayerService", "Музыка закончилась");
            stopSelf();
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("PlayerService", "onStartCommand");

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.d("PlayerService", "mediaPlayer.start()");
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("PlayerService", "onDestroy");

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        stopForeground(true);

        super.onDestroy();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Student Notification",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        channel.setDescription("MIREA ServiceApp Channel");

        NotificationManager manager = getSystemService(NotificationManager.class);

        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
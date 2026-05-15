package ru.mirea.vakhrushevra.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import ru.mirea.vakhrushevra.audiorecord.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 200;

    private ActivityMainBinding binding;
    private String recordFilePath;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private boolean isRecording = false;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        File musicDirectory = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (musicDirectory != null) {
            File recordFile = new File(musicDirectory, "record.3gp");
            recordFilePath = recordFile.getAbsolutePath();
        }

        checkPermission();
        updateButtons();

        binding.recordButton.setOnClickListener(view -> {
            if (!hasAudioPermission()) {
                Toast.makeText(this, "Нет разрешения на запись аудио", Toast.LENGTH_SHORT).show();
                checkPermission();
                return;
            }

            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        binding.playButton.setOnClickListener(view -> {
            if (!isPlaying) {
                startPlaying();
            } else {
                stopPlaying();
            }
        });
    }

    private void checkPermission() {
        if (!hasAudioPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE_PERMISSION
            );
        }
    }

    private boolean hasAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startRecording() {
        if (recordFilePath == null) {
            Toast.makeText(this, "Не удалось создать файл", Toast.LENGTH_SHORT).show();
            return;
        }

        releasePlayer();
        releaseRecorder();

        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            binding.textViewStatus.setText("Идет запись...");
            updateButtons();
            Toast.makeText(this, "Запись началась", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка подготовки записи", Toast.LENGTH_SHORT).show();
            releaseRecorder();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Ошибка запуска записи", Toast.LENGTH_SHORT).show();
            releaseRecorder();
        }
    }

    private void stopRecording() {
        if (recorder == null) {
            return;
        }

        try {
            recorder.stop();
            binding.textViewStatus.setText("Запись сохранена");
            Toast.makeText(this, "Запись остановлена", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            deleteRecordFile();
            binding.textViewStatus.setText("Запись слишком короткая");
            Toast.makeText(this, "Запись слишком короткая", Toast.LENGTH_SHORT).show();
        }

        releaseRecorder();
        isRecording = false;
        updateButtons();
    }

    private void startPlaying() {
        if (recordFilePath == null) {
            Toast.makeText(this, "Файл записи не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        File audioFile = new File(recordFilePath);
        if (!audioFile.exists()) {
            Toast.makeText(this, "Сначала сделайте запись", Toast.LENGTH_SHORT).show();
            return;
        }

        releasePlayer();
        player = new MediaPlayer();

        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
            isPlaying = true;
            binding.textViewStatus.setText("Воспроизведение...");
            updateButtons();

            player.setOnCompletionListener(mediaPlayer -> {
                stopPlayingCompleted();
            });

            Toast.makeText(this, "Воспроизведение началось", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
            releasePlayer();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
            releasePlayer();
        }
    }

    private void stopPlaying() {
        if (player == null) {
            return;
        }

        releasePlayer();
        isPlaying = false;
        binding.textViewStatus.setText("Воспроизведение остановлено");
        updateButtons();
    }

    private void stopPlayingCompleted() {
        releasePlayer();
        isPlaying = false;
        binding.textViewStatus.setText("Воспроизведение завершено");
        updateButtons();
    }

    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void deleteRecordFile() {
        if (recordFilePath == null) {
            return;
        }

        File audioFile = new File(recordFilePath);
        if (audioFile.exists()) {
            audioFile.delete();
        }
    }

    private void updateButtons() {
        binding.recordButton.setText(isRecording ? "Остановить запись" : "Начать запись");
        binding.playButton.setText(isPlaying ? "Остановить воспроизведение" : "Воспроизвести");
        binding.recordButton.setEnabled(!isPlaying);

        if (isRecording) {
            binding.playButton.setEnabled(false);
            return;
        }

        if (recordFilePath == null) {
            binding.playButton.setEnabled(false);
            return;
        }

        File audioFile = new File(recordFilePath);
        binding.playButton.setEnabled(audioFile.exists());
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (recorder != null) {
            stopRecording();
        }

        if (player != null) {
            stopPlaying();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение на микрофон получено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Разрешение на микрофон не получено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

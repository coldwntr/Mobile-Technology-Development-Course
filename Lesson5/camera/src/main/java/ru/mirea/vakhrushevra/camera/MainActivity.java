package ru.mirea.vakhrushevra.camera;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.vakhrushevra.camera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_PROVIDER_SUFFIX = ".fileprovider";
    private static final String IMAGES_DIRECTORY = "images";
    private static final String STATE_CURRENT_PHOTO_PATH = "state_current_photo_path";

    private ActivityMainBinding binding;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Nullable
    private Uri currentPhotoUri;

    @Nullable
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLaunchers();
        restoreState(savedInstanceState);

        binding.buttonTakePhoto.setOnClickListener(view -> requestCameraAndOpen());
        binding.imageView.setOnClickListener(view -> requestCameraAndOpen());
    }

    private void initLaunchers() {
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        showToast("Разрешение на камеру не выдано");
                    }
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    revokeCurrentUriPermission();

                    if (result.getResultCode() == Activity.RESULT_OK && hasCapturedPhoto()) {
                        showCapturedPhoto();
                        showToast("Фото сохранено и показано на экране");
                    } else {
                        deleteIncompletePhoto();
                        showToast("Съёмка отменена");
                    }
                }
        );
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        currentPhotoPath = savedInstanceState.getString(STATE_CURRENT_PHOTO_PATH);
        if (currentPhotoPath == null) {
            return;
        }

        File photoFile = new File(currentPhotoPath);
        if (!photoFile.exists()) {
            currentPhotoPath = null;
            return;
        }

        currentPhotoUri = buildPhotoUri(photoFile);
        showCapturedPhoto();
    }

    private void requestCameraAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile;
        try {
            photoFile = createImageFile();
        } catch (IOException exception) {
            showToast("Не удалось подготовить файл для фото");
            return;
        }

        currentPhotoUri = buildPhotoUri(photoFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cameraIntent.setClipData(ClipData.newUri(getContentResolver(), "captured_photo", currentPhotoUri));

        try {
            takePictureLauncher.launch(cameraIntent);
        } catch (ActivityNotFoundException exception) {
            deleteIncompletePhoto();
            showToast("Камера на эмуляторе недоступна");
        } catch (SecurityException exception) {
            deleteIncompletePhoto();
            showToast("Нет доступа для запуска камеры");
        }
    }

    private File createImageFile() throws IOException {
        File imagesDirectory = new File(getFilesDir(), IMAGES_DIRECTORY);
        if (!imagesDirectory.exists() && !imagesDirectory.mkdirs()) {
            throw new IOException("Unable to create images directory");
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File imageFile = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", imagesDirectory);
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private Uri buildPhotoUri(File photoFile) {
        return FileProvider.getUriForFile(
                this,
                getPackageName() + FILE_PROVIDER_SUFFIX,
                photoFile
        );
    }

    private boolean hasCapturedPhoto() {
        if (currentPhotoPath == null) {
            return false;
        }

        File photoFile = new File(currentPhotoPath);
        return photoFile.exists() && photoFile.length() > 0;
    }

    private void showCapturedPhoto() {
        if (currentPhotoUri == null) {
            return;
        }

        binding.imageView.setImageURI(null);
        binding.imageView.setImageURI(currentPhotoUri);
    }

    private void deleteIncompletePhoto() {
        if (currentPhotoPath != null) {
            File photoFile = new File(currentPhotoPath);
            if (photoFile.exists() && photoFile.length() == 0L) {
                // Delete empty files left after a canceled capture.
                photoFile.delete();
            }
        }

        currentPhotoPath = null;
        currentPhotoUri = null;
    }

    private void revokeCurrentUriPermission() {
        if (currentPhotoUri == null) {
            return;
        }

        revokeUriPermission(
                currentPhotoUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        );
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_PHOTO_PATH, currentPhotoPath);
    }
}

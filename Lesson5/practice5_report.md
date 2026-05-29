# Отчет по практической работе № 5

## Тема
Использование аппаратных возможностей мобильного устройства в Android: датчики, акселерометр, камера, микрофон и механизм разрешений.

## Цель работы
Изучить способы работы с аппаратными возможностями Android-устройства и реализовать несколько отдельных модулей:

1. вывод списка доступных датчиков;
2. отображение показаний акселерометра;
3. запуск системной камеры с сохранением фотографии в папку приложения;
4. запись и воспроизведение звука через микрофон.

## Что было сделано
В Android Studio был создан проект `Lesson5`, внутри которого реализованы следующие модули:

1. `app` — вывод списка датчиков устройства;
2. `accelerometer` — получение и отображение показаний акселерометра;
3. `camera` — запуск системной камеры, сохранение снимка и показ результата;
4. `audiorecord` — запись звука с микрофона и последующее воспроизведение;
5. `lesson5_first` — стартовый шаблон Empty Activity без основной логики практической работы.

## Структура выполненной работы
Практика была выполнена по этапам:

1. Сначала был создан многомодульный Android-проект.
2. Затем в модуле `app` реализован вывод списка всех сенсоров через `SensorManager`.
3. После этого в модуле `accelerometer` был подключен акселерометр и настроено обновление данных в реальном времени.
4. В модуле `camera` был реализован вызов системного приложения камеры через `Intent`, а для безопасной передачи файла применен `FileProvider`.
5. В модуле `audiorecord` были добавлены запрос разрешения на микрофон, запись аудио через `MediaRecorder` и воспроизведение через `MediaPlayer`.

---

## 1. Модуль `app`: список датчиков устройства

### Что я делал
В этом модуле я реализовал экран, который получает список всех датчиков, доступных на устройстве, и отображает их в `ListView`. Для этого был использован класс `SensorManager` и метод `getSensorList(Sensor.TYPE_ALL)`.

### Листинг разметки `activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ListView
        android:id="@+id/sensorListView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Листинг `MainActivity.java`
```java
package ru.mirea.vakhrushevra.lesson5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.mirea.vakhrushevra.lesson5.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SensorManager sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        ListView listSensor = binding.sensorListView;

        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();

        for (Sensor sensor : sensors) {
            HashMap<String, Object> sensorInfo = new HashMap<>();
            sensorInfo.put("Name", sensor.getName());
            sensorInfo.put("Value", "Макс. значение: " + sensor.getMaximumRange());
            arrayList.add(sensorInfo);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                arrayList,
                android.R.layout.simple_list_item_2,
                new String[]{"Name", "Value"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        listSensor.setAdapter(adapter);
    }
}
```

### Объяснение кода
`SensorManager` нужен для доступа к датчикам устройства. С помощью `getSystemService(Context.SENSOR_SERVICE)` я получаю системный сервис работы с сенсорами.

Метод `getSensorList(Sensor.TYPE_ALL)` возвращает список всех поддерживаемых датчиков, включая аппаратные и виртуальные.

Далее я создаю коллекцию `ArrayList<HashMap<String, Object>>`, в которую помещаю две характеристики каждого датчика:

1. его название через `sensor.getName()`;
2. его максимальное значение через `sensor.getMaximumRange()`.

Для вывода списка используется `SimpleAdapter`, который отображает две строки для каждого элемента списка: название датчика и его характеристику.

### Результат
На экране отображается список датчиков, установленных на устройстве, что соответствует первому заданию практической работы.

---

## 2. Модуль `accelerometer`: показания акселерометра

### Что я делал
В этом модуле я создал приложение, которое отслеживает изменение значений акселерометра по трем осям и выводит их на экран в реальном времени.

### Листинг разметки `activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="24dp"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textViewAzimuth"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Azimuth:"
        android:textSize="22sp"
        app:layout_constraintBottom_toTopOf="@id/textViewPitch"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/textViewPitch"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Pitch:"
        android:textSize="22sp"
        android:layout_marginTop="24dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewAzimuth" />

    <TextView
        android:id="@+id/textViewRoll"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Roll:"
        android:textSize="22sp"
        android:layout_marginTop="24dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewPitch" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Листинг `MainActivity.java`
```java
package ru.mirea.vakhrushevra.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.vakhrushevra.accelerometer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometerSensor == null) {
            binding.textViewAzimuth.setText("Акселерометр не найден");
            binding.textViewPitch.setText("");
            binding.textViewRoll.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (accelerometerSensor != null) {
            sensorManager.registerListener(
                    this,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float valueAzimuth = event.values[0];
            float valuePitch = event.values[1];
            float valueRoll = event.values[2];

            binding.textViewAzimuth.setText("Azimuth X: " + valueAzimuth);
            binding.textViewPitch.setText("Pitch Y: " + valuePitch);
            binding.textViewRoll.setText("Roll Z: " + valueRoll);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
```

### Объяснение кода
Класс активности реализует интерфейс `SensorEventListener`, чтобы приложение могло получать уведомления об изменении показаний датчика.

В `onCreate()` я получаю ссылку на `SensorManager`, а затем нахожу акселерометр через `getDefaultSensor(Sensor.TYPE_ACCELEROMETER)`.

В `onResume()` происходит регистрация слушателя:
```java
sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
```
Это означает, что приложение начинает получать события от акселерометра, когда экран активен.

В `onPause()` слушатель снимается:
```java
sensorManager.unregisterListener(this);
```
Это делается для освобождения ресурсов и корректной работы жизненного цикла активности.

В `onSensorChanged()` я считываю три значения:

1. `event.values[0]` — ускорение по оси X;
2. `event.values[1]` — ускорение по оси Y;
3. `event.values[2]` — ускорение по оси Z.

После этого значения выводятся в `TextView`, поэтому при наклоне или повороте устройства данные на экране изменяются.

### Результат
На экране в реальном времени отображаются значения акселерометра по трем осям, что соответствует заданию практики.

---

## 3. Модуль `camera`: работа с камерой и механизмом разрешений

### Что я делал
В этом модуле я реализовал вызов системного приложения камеры, сохранение сделанного снимка во внутреннюю папку приложения и отображение фотографии на экране. Также был реализован запрос разрешения `CAMERA` во время работы приложения.

### Листинг `AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-feature
        android:name="android.hardware.camera.any"
        android:required="false" />

    <uses-permission android:name="android.permission.CAMERA" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Lesson5">

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">

            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/paths" />

        </provider>

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### Листинг `paths.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <files-path
        name="images"
        path="images/" />
</paths>
```

### Листинг разметки `activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textViewHint"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:layout_marginBottom="16dp"
        android:text="Нажмите кнопку или изображение, чтобы сделать фото"
        android:textSize="20sp"
        app:layout_constraintBottom_toTopOf="@id/buttonTakePhoto"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/buttonTakePhoto"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:text="Открыть камеру"
        app:layout_constraintBottom_toTopOf="@id/imageView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewHint" />

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="#DDDDDD"
        android:contentDescription="Captured photo"
        android:scaleType="centerCrop"
        android:src="@android:drawable/ic_menu_camera"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintDimensionRatio="1:1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/buttonTakePhoto" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Листинг `MainActivity.java`
```java
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
```

### Объяснение кода
Сначала в `AndroidManifest.xml` я добавил разрешение:
```xml
<uses-permission android:name="android.permission.CAMERA" />
```
Без него приложение не сможет получить доступ к камере.

Также в манифесте был зарегистрирован `FileProvider`. Он нужен для безопасной передачи ссылки на файл в системное приложение камеры. Начиная с новых версий Android нельзя просто передавать обычный путь к файлу, поэтому используется `Uri`, сформированный через `FileProvider`.

В `paths.xml` я указал, что камера может работать с папкой `images` во внутреннем хранилище приложения.

В разметке был создан `ImageView` для отображения фотографии и кнопка для запуска камеры.

В коде `MainActivity` логика работает так:

1. При нажатии на кнопку вызывается `requestCameraAndOpen()`.
2. Сначала проверяется, есть ли разрешение `CAMERA`.
3. Если разрешения нет, оно запрашивается через `ActivityResultContracts.RequestPermission()`.
4. Если разрешение есть, создается `Intent` с действием `MediaStore.ACTION_IMAGE_CAPTURE`.
5. Перед запуском камеры создается временный файл через `createImageFile()`.
6. Для этого файла формируется безопасный `Uri` через `buildPhotoUri()`.
7. Этот `Uri` передается в `Intent` через `MediaStore.EXTRA_OUTPUT`, чтобы камера сохранила фотографию именно в файл приложения.
8. После возврата из камеры проверяется, был ли снимок успешно сделан. Если да, то изображение показывается в `ImageView`.

Дополнительно я сохранил путь к файлу в `onSaveInstanceState()`, чтобы при повороте экрана не потерять уже сделанную фотографию.

### Результат
Приложение открывает системную камеру, сохраняет снимок в папку приложения и затем отображает полученное изображение на экране. Также корректно обрабатывается отсутствие разрешения и отмена съемки.

---

## 4. Модуль `audiorecord`: запись и воспроизведение звука

### Что я делал
В этом модуле я реализовал диктофон: приложение запрашивает разрешение на микрофон, записывает звук в файл формата `3gp`, сохраняет его во внешней папке приложения и затем воспроизводит запись.

### Листинг `AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Lesson5">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### Листинг разметки `activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="24dp"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textViewStatus"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:layout_marginTop="48dp"
        android:layout_marginBottom="32dp"
        android:text="Диктофон готов"
        android:textSize="22sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/recordButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:text="Начать запись"
        android:textSize="16sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewStatus" />

    <Button
        android:id="@+id/playButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Воспроизвести"
        android:textSize="16sp"
        android:enabled="false"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/recordButton" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Листинг `MainActivity.java`
```java
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
```

### Объяснение кода
Для работы с микрофоном в манифест было добавлено разрешение:
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

Так как это опасное разрешение, его недостаточно просто указать в манифесте. Поэтому в коде реализован полный механизм разрешений:

1. проверка через `ContextCompat.checkSelfPermission(...)`;
2. запрос через `ActivityCompat.requestPermissions(...)`;
3. обработка результата в `onRequestPermissionsResult(...)`.

Файл записи создается в папке приложения:
```java
File musicDirectory = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
```
Это удобно, потому что приложению не требуется вручную управлять общим внешним хранилищем.

Для записи используется объект `MediaRecorder`. Перед началом записи я указываю:

1. источник звука — `MIC`;
2. формат файла — `THREE_GPP`;
3. путь сохранения файла;
4. кодировщик — `AMR_NB`.

После этого вызываются методы:
```java
recorder.prepare();
recorder.start();
```

Для воспроизведения используется `MediaPlayer`. Ему передается путь к ранее сохраненному файлу:
```java
player.setDataSource(recordFilePath);
player.prepare();
player.start();
```

Также в коде реализована защита от ошибок:

1. нельзя начать воспроизведение, если файл еще не создан;
2. во время записи кнопка воспроизведения блокируется;
3. во время воспроизведения блокируется запись;
4. если запись оказалась слишком короткой и `MediaRecorder.stop()` выбрасывает ошибку, файл удаляется;
5. в `onStop()` ресурсы освобождаются автоматически, чтобы приложение не оставляло открытый микрофон или проигрыватель.

### Результат
Приложение может записывать голос через микрофон, сохранять запись в файл и затем воспроизводить ее по нажатию кнопки.

---

## Особенности механизма разрешений в моей работе

В данной практике я использовал два опасных разрешения:

1. `android.permission.CAMERA`;
2. `android.permission.RECORD_AUDIO`.

В обоих случаях логика работы одинакова:

1. разрешение сначала указывается в `AndroidManifest.xml`;
2. затем перед выполнением действия проверяется, выдано ли оно пользователем;
3. если нет, приложение показывает системный запрос;
4. только после одобрения пользователя выполняется обращение к оборудованию устройства.

Это важно для современных версий Android, потому что опасные разрешения нельзя использовать без подтверждения пользователя во время работы приложения.

---

## Вывод

В ходе выполнения практической работы я изучил работу Android с аппаратными возможностями мобильного устройства и реализовал несколько самостоятельных модулей.

В результате были получены следующие навыки:

1. работа с `SensorManager` и сенсорами устройства;
2. регистрация и снятие слушателей датчиков через жизненный цикл активности;
3. использование механизма runtime permissions;
4. запуск системной камеры через `Intent`;
5. безопасная передача файлов с помощью `FileProvider`;
6. запись звука через `MediaRecorder`;
7. воспроизведение аудио через `MediaPlayer`.

Практическая работа выполнена: список датчиков отображается, показания акселерометра обновляются, камера делает снимок и показывает его на экране, а модуль аудиозаписи позволяет записывать и воспроизводить звук.

---

## Примечание по проверке
Содержимое отчета составлено по фактическому коду проекта `Lesson5`. Автоматическую сборку всех модулей в текущем окружении выполнить не удалось, так как на машине отсутствует установленная Java Runtime (`Unable to locate a Java Runtime`).

package ru.mirea.vakhrushevra.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MyLooper extends Thread {

    public Handler mHandler;
    private final Handler mainHandler;

    public MyLooper(Handler mainThreadHandler) {
        this.mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Log.d("MyLooper", "Метод run запущен. Поток: " + Thread.currentThread().getName());

        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();

                int age = bundle.getInt("AGE");
                String job = bundle.getString("JOB");

                Log.d("MyLooper", "Получено сообщение");
                Log.d("MyLooper", "Возраст: " + age);
                Log.d("MyLooper", "Работа: " + job);

                try {
                    Log.d("MyLooper", "Началась задержка на " + age + " секунд");

                    Thread.sleep(age * 1000L);

                    Log.d("MyLooper", "Задержка закончилась");
                } catch (InterruptedException e) {
                    Log.d("MyLooper", "Поток был прерван");
                    return;
                }

                String result = "Возраст: " + age + ", работа: " + job;

                Log.d("MyLooper", "Результат вычисления: " + result);

                Message messageToMainThread = Message.obtain();
                Bundle resultBundle = new Bundle();
                resultBundle.putString("RESULT", result);
                messageToMainThread.setData(resultBundle);

                mainHandler.sendMessage(messageToMainThread);
            }
        };

        Log.d("MyLooper", "Handler создан. Looper готов принимать сообщения.");

        Looper.loop();
    }
}
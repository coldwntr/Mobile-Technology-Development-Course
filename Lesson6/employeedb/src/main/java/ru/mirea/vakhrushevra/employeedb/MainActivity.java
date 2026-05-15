package ru.mirea.vakhrushevra.employeedb;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "ROOM_DATABASE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setTextSize(22);
        textView.setPadding(30, 30, 30, 30);
        setContentView(textView);

        AppDatabase db = App.getInstance().getDatabase();

        EmployeeDao employeeDao = db.employeeDao();

        Employee hero1 = new Employee();
        hero1.name = "Batman";
        hero1.universe = "DC";
        hero1.power = "Money";

        Employee hero2 = new Employee();
        hero2.name = "Spider-Man";
        hero2.universe = "Marvel";
        hero2.power = "Spider abilities";

        Employee hero3 = new Employee();
        hero3.name = "Iron Man";
        hero3.universe = "Marvel";
        hero3.power = "Technology";

        employeeDao.insert(hero1);
        employeeDao.insert(hero2);
        employeeDao.insert(hero3);

        List<Employee> heroes = employeeDao.getAll();

        String result = "";

        for (Employee hero : heroes) {
            result = result + hero.id + ". "
                    + hero.name + " | "
                    + hero.universe + " | "
                    + hero.power + "\n";

            Log.d(TAG, hero.id + " "
                    + hero.name + " "
                    + hero.universe + " "
                    + hero.power);
        }

        textView.setText(result);
    }
}
package ru.mirea.vakhrushevra.simplefragmentapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment1;
    private Fragment fragment2;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new FirstFragment();
        fragment2 = new SecondFragment();

        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.fragmentContainer) != null) {
            if (savedInstanceState == null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment1)
                        .commit();
            }
        }
    }

    public void onClick(View view) {
        if (findViewById(R.id.fragmentContainer) == null) {
            return;
        }

        int id = view.getId();

        if (id == R.id.btnFirstFragment) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment1)
                    .commit();
        } else if (id == R.id.btnSecondFragment) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment2)
                    .commit();
        }
    }
}
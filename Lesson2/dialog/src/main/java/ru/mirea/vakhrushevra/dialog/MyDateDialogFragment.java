package ru.mirea.vakhrushevra.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyDateDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(
                requireActivity(),
                (view, year1, month1, dayOfMonth) -> {
                    String dateText = String.format("%02d.%02d.%d", dayOfMonth, month1 + 1, year1);
                    ((MainActivity) requireActivity()).onDateOkClicked(dateText);
                },
                year,
                month,
                day
        );
    }
}
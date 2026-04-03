package ru.mirea.vakhrushevra.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyTimeDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(
                requireActivity(),
                (view, hourOfDay, minute1) -> {
                    String timeText = String.format("%02d:%02d", hourOfDay, minute1);
                    ((MainActivity) requireActivity()).onTimeOkClicked(timeText);
                },
                hour,
                minute,
                true
        );
    }
}
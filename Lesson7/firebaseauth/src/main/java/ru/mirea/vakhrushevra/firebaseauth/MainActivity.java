package ru.mirea.vakhrushevra.firebaseauth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private com.google.firebase.auth.FirebaseAuth mAuth;

    private TextView statusTextView;
    private TextView detailTextView;

    private EditText emailEditText;
    private EditText passwordEditText;

    private LinearLayout emailPasswordFields;
    private LinearLayout emailPasswordButtons;
    private LinearLayout signedInButtons;

    private Button signInButton;
    private Button createAccountButton;
    private Button signOutButton;
    private Button verifyEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        statusTextView = findViewById(R.id.statusTextView);
        detailTextView = findViewById(R.id.detailTextView);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        emailPasswordFields = findViewById(R.id.emailPasswordFields);
        emailPasswordButtons = findViewById(R.id.emailPasswordButtons);
        signedInButtons = findViewById(R.id.signedInButtons);

        signInButton = findViewById(R.id.signInButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        signOutButton = findViewById(R.id.signOutButton);
        verifyEmailButton = findViewById(R.id.verifyEmailButton);

        createAccountButton.setOnClickListener(v -> createAccount(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString()
        ));

        signInButton.setOnClickListener(v -> signIn(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString()
        ));

        signOutButton.setOnClickListener(v -> signOut());

        verifyEmailButton.setOnClickListener(v -> sendEmailVerification());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Введите email");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Введите пароль");
            valid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Пароль минимум 6 символов");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Toast.makeText(
                                MainActivity.this,
                                "Аккаунт создан",
                                Toast.LENGTH_SHORT
                        ).show();

                        updateUI(user);
                    } else {
                        Toast.makeText(
                                MainActivity.this,
                                "Ошибка регистрации: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        updateUI(null);
                    }
                });
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Toast.makeText(
                                MainActivity.this,
                                "Вход выполнен",
                                Toast.LENGTH_SHORT
                        ).show();

                        updateUI(user);
                    } else {
                        Toast.makeText(
                                MainActivity.this,
                                "Ошибка входа: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        verifyEmailButton.setEnabled(false);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            verifyEmailButton.setEnabled(true);
            return;
        }

        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    verifyEmailButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(
                                MainActivity.this,
                                "Письмо отправлено на " + user.getEmail(),
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                MainActivity.this,
                                "Ошибка отправки письма",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            statusTextView.setText(
                    "Email User: " + user.getEmail()
                            + " (verified: "
                            + user.isEmailVerified()
                            + ")"
            );

            detailTextView.setText(
                    "Firebase UID: " + user.getUid()
            );

            emailPasswordFields.setVisibility(View.GONE);
            emailPasswordButtons.setVisibility(View.GONE);
            signedInButtons.setVisibility(View.VISIBLE);

            verifyEmailButton.setEnabled(!user.isEmailVerified());

        } else {
            statusTextView.setText("Signed Out");
            detailTextView.setText("");

            emailPasswordFields.setVisibility(View.VISIBLE);
            emailPasswordButtons.setVisibility(View.VISIBLE);
            signedInButtons.setVisibility(View.GONE);
        }
    }
}
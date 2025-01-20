package com.image.ibvtask.activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.image.ibvtask.MainActivity;
import com.image.ibvtask.Models.LoginResponse;
import com.image.ibvtask.R;
import com.image.ibvtask.SecuredStorage;
import com.image.ibvtask.ViewModel.IBVViewModel;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    IBVViewModel viewModel;

    BiometricManager biometricManager;

    BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt biometricPrompt;

    KeyguardManager keyguardManager;

    boolean isBioMetrics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        biometricManager = BiometricManager.from(this);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT>29) {

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for my app")
                    .setSubtitle("Log in using your biometric credential")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                    .build();




        }

        biometricPrompt = new BiometricPrompt(this,new BioMetricCallback());





        viewModel = new ViewModelProvider(this).get(IBVViewModel.class);

        viewModel.getLoginLiveData().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {

                if (loginResponse!=null) {

                    if (loginResponse.getSuccess()) {

                        SecuredStorage s = new SecuredStorage(LoginActivity.this);
                        s.storeData(loginResponse.getToken());

//                        SharedPreferences s = getSharedPreferences("credentials",MODE_PRIVATE);
//                        SharedPreferences.Editor editor = s.edit();
//                        editor.putString("token",loginResponse.getToken());
//                        editor.putBoolean("isLoggedIn",true);
//                        editor.commit();

                        Toast.makeText(LoginActivity.this, "Logged In.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK|FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);


                    } else {

                        login.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                        login.setClickable(true);
                        login.setText("Login");

                        Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();


                    }
                }
            }
        });

        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isBioMetrics) {

                    login.setBackgroundColor(getResources().getColor(R.color.grey));
                    login.setClickable(false);
                    login.setText("Logging In...");

                    viewModel.getLogin();


                } else {

                    Toast.makeText(LoginActivity.this, "Can't login without biometrics", Toast.LENGTH_SHORT).show();
                }



            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT<=Build.VERSION_CODES.Q) {


                switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        isBioMetrics = true;

                        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Biometric login for my app")
                                .setSubtitle("Log in using your biometric credential")
                                .setNegativeButtonText("Cancel")
                                .build();

                        biometricPrompt.authenticate(promptInfo);


                        Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        isBioMetrics = false;
                        Log.e("MY_APP_TAG", "No biometric features available on this device.");
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        isBioMetrics = false;
                        Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        // Prompts the user to create credentials that your app accepts.
                        final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                        startActivityForResult(enrollIntent, 100);
                        break;
                }



        } else {

            Log.d(LoginActivity.class.getName(), "onCreate: ");

            switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    isBioMetrics = true;

                    promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric login for my app")
                            .setSubtitle("Log in using your biometric credential")
                            .setNegativeButtonText("Cancel")
                            .build();

                    biometricPrompt.authenticate(promptInfo);


                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    isBioMetrics = false;
                    Log.e("MY_APP_TAG", "No biometric features available on this device.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    isBioMetrics = false;
                    Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    // Prompts the user to create credentials that your app accepts.
                    final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                    startActivityForResult(enrollIntent, 100);
                    break;
            }


        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {


        }
    }

    private class BioMetricCallback extends BiometricPrompt.AuthenticationCallback {

        public BioMetricCallback() {
            super();
        }

        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);

            Toast.makeText(getApplicationContext(),
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);

            Toast.makeText(getApplicationContext(),
                    "Authentication succeeded!", Toast.LENGTH_SHORT).show();

            SecuredStorage securedStorage = new SecuredStorage(LoginActivity.this);
            String token = securedStorage.retrieveData();

            if (token!=null && !token.equals("")) {

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK|FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();

            Toast.makeText(getApplicationContext(), "Authentication failed",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }
}

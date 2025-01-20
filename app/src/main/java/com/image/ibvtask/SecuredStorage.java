package com.image.ibvtask;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecuredStorage {
    private Context context;

    public SecuredStorage(Context context) {
        this.context = context;
    }

    public void storeData(String token) {
        try {
            // Generate a master key for encryption
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "credentials",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // Store encrypted data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", token);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String retrieveData() {

        String token = "";
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "credentials",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // Retrieve decrypted data
            token = sharedPreferences.getString("token", "");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;


    }

    public void clearToken() {


        try {
            // Generate a master key for encryption
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "credentials",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // Store encrypted data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", null);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


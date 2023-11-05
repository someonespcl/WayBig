package com.waybig.preferences;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyGenParameterSpec;
import java.security.KeyStore;

public class EncryptionHelper {
    private static final String KEY_ALIAS = "ProfileImageEncryptionKey";
    
    public static Cipher initCipher() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build();

            keyGenerator.init(keyGenParameterSpec);
            keyGenerator.generateKey();

            Cipher cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_GCM + "/"
                        + KeyProperties.ENCRYPTION_PADDING_NONE);

            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] encryptData(Cipher cipher, byte[] data) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] decryptData(Cipher cipher, byte[] encryptedData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static SecretKey getSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package net.power51.react;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.provider.Contacts.SettingsColumns.KEY;

public class RNCipherModule extends ReactContextBaseJavaModule {
    private static final int BUFF_SIZE = 1024 * 64;

    private final ReactApplicationContext reactContext;

    public RNCipherModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNCipher";
    }

    static private byte[] hexToBytes(String hex) {
        byte[] bytes = new BigInteger(hex, 16).toByteArray();
        int offset = 0;
        for(; offset < bytes.length && bytes[offset] == 0; ++offset);
        byte[] result = new byte[bytes.length - offset];
        System.arraycopy(result, 0, bytes, offset, bytes.length - offset);
        return result;
    }

    private static class PromiseWrapper {
        public PromiseWrapper(Promise promise, Throwable reason) {
            this.promise = promise;
            this.reason = reason;
            this.size = 0;
        }
        public PromiseWrapper(Promise promise, int size) {
            this.promise = promise;
            this.reason = null;
            this.size = size;
        }
        public void done() {
            if (null != reason) {
                promise.reject(reason);
            } else {
                promise.resolve(size);
            }
        }


        private final Promise promise;
        private final Throwable reason;
        private final int size;
    }

    private final AsyncTask task = new AsyncTask<Object, Object, PromiseWrapper>() {
        @Override
        protected void onPostExecute(PromiseWrapper result) {
            result.done();
        }

        @Override
        protected PromiseWrapper doInBackground(Object[] objects) {
            final String alg =              (String)objects[0];
            final String key =              (String)objects[1];
            final String keyAlg =           (String)objects[2];
            final String iv =               (String)objects[3];
            final String encryptFilePath =  (String)objects[4];
            final String decryptFilePath =  (String)objects[5];
            final Promise promise =         (Promise)objects[6];
            int size = 0;

            try {
                Cipher cipher = Cipher.getInstance(alg);
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hexToBytes(key), keyAlg), new IvParameterSpec(hexToBytes(iv)));
                InputStream inputStream = new FileInputStream(encryptFilePath);
                CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
                FileOutputStream fileOutputStream = new FileOutputStream(decryptFilePath);
                byte[] temp = new byte[BUFF_SIZE];
                int ret = 0;

                while ((ret = cipherInputStream.read(temp)) > 0) {
                    fileOutputStream.write(temp, 0, ret);
                    size += ret;
                }

                return new PromiseWrapper(promise, size);

            } catch (NoSuchAlgorithmException exception) {
                Log.e("RNCipher", exception.toString());
                return new PromiseWrapper(promise, exception);
            } catch (NoSuchPaddingException exception) {
                Log.e("RNCipher", exception.toString());
                return new PromiseWrapper(promise, exception);
            } catch (InvalidKeyException exception) {
                Log.e("RNCipher", exception.toString());
                return new PromiseWrapper(promise, exception);
            } catch (InvalidAlgorithmParameterException exception) {
                Log.e("RNCipher", exception.toString());
                return new PromiseWrapper(promise, exception);
            } catch (FileNotFoundException exception) {
                Log.e("RNCipher", exception.toString());
                return new PromiseWrapper(promise, exception);
            } catch (IOException exception) {
                Log.e("RNCipher", exception.toString());
                return new PromiseWrapper(promise, exception);
            }
        }
    };

    @ReactMethod
    public void decryptFile(String alg, String key, String keyAlg, String iv, String encryptFilePath, String decryptFilePath, Promise promise) {
        task.execute(alg, key, keyAlg, iv, encryptFilePath, decryptFilePath, promise);
    }
}

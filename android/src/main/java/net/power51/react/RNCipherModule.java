
package net.power51.react;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Base64;
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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
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
        final int offset = bytes.length - hex.length() / 2;
        byte[] result = new byte[bytes.length - offset];
        System.arraycopy(bytes, offset, result, 0, bytes.length - offset);
        return result;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
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

    static class DecryptTask extends AsyncTask<Object, Object, PromiseWrapper> {
        @Override
        protected void onPostExecute(PromiseWrapper result) {
            result.done();
        }

        @Override
        protected PromiseWrapper doInBackground(Object[] objects) {
            final String alg =              (String)objects[0];
            final String keyString =              (String)objects[1];
            final String keyAlg =           (String)objects[2];
            final String ivString =               (String)objects[3];
            final String encryptFilePath =  (String)objects[4];
            final String decryptFilePath =  (String)objects[5];
            final Promise promise =         (Promise)objects[6];
            int size = 0;
            PromiseWrapper ret = null;

            InputStream inputStream = null;
            CipherInputStream cipherInputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                Cipher cipher = Cipher.getInstance(alg);
                byte[] key = hexToBytes(keyString);
                byte[] iv = hexToBytes(ivString);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, keyAlg), ivParameterSpec);
                inputStream = new FileInputStream(encryptFilePath);
                cipherInputStream = new CipherInputStream(inputStream, cipher);
                fileOutputStream = new FileOutputStream(decryptFilePath);
                byte[] temp = new byte[BUFF_SIZE];
                int ret = 0;

                while ((ret = cipherInputStream.read(temp)) > 0) {
                    fileOutputStream.write(temp, 0, ret);
                    size += ret;
                }

                ret = new PromiseWrapper(promise, size);
            } catch (NoSuchAlgorithmException exception) {
                Log.e("RNCipher", exception.toString());
                ret =  new PromiseWrapper(promise, exception);
            } catch (NoSuchPaddingException exception) {
                Log.e("RNCipher", exception.toString());
                ret = new PromiseWrapper(promise, exception);
            } catch (InvalidKeyException exception) {
                Log.e("RNCipher", exception.toString());
                ret = new PromiseWrapper(promise, exception);
            } catch (InvalidAlgorithmParameterException exception) {
                Log.e("RNCipher", exception.toString());
                ret = new PromiseWrapper(promise, exception);
            } catch (FileNotFoundException exception) {
                Log.e("RNCipher", exception.toString());
                ret = new PromiseWrapper(promise, exception);
            } catch (IOException exception) {
                Log.e("RNCipher", exception.toString());
                ret = new PromiseWrapper(promise, exception);
            } finally {
              if (fileOutputStream != null) {
                fileOutputStream.close();
              }
              if (cipherInputStream != null) {
                cipherInputStream.close();
              }
              if (inputStream != null) {
                inputStream.close();
              }
            }
            return ret;
        }
    };

    @ReactMethod
    public void decryptFile(String alg, String key, String keyAlg, String iv, String encryptFilePath, String decryptFilePath, Promise promise) {
        DecryptTask task = new DecryptTask();
        task.execute(alg, key, keyAlg, iv, encryptFilePath, decryptFilePath, promise);
    }

    @ReactMethod
    public void rsaDecryptHex(String alg, String privateKeyBase64, String dataHex, Promise promise) {
        try {
            byte[] buffer = Base64.decode(privateKeyBase64, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            byte[] output = cipher.doFinal(hexToBytes(dataHex));
            promise.resolve(bytesToHex(output));
        } catch (NoSuchAlgorithmException exception) {
            promise.reject(exception);
        } catch (NoSuchPaddingException exception) {
            promise.reject(exception);
        } catch (InvalidKeySpecException exception) {
            promise.reject(exception);
        } catch (InvalidKeyException exception) {
            promise.reject(exception);
        } catch (IllegalBlockSizeException exception) {
            promise.reject(exception);
        } catch (BadPaddingException exception) {
            promise.reject(exception);
        }
    }
}

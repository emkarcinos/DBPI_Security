package Main;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

public class AES {

    public static byte[] generateIv(){
        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        randomSecureRandom.nextBytes(iv);
        return iv;
    }

    public static byte[] encrypt(byte[] data, SecretKey secretKey, byte[] iv)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(data);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static byte[] decrypt(byte[] data, SecretKey secretKey, byte[] iv)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(data);
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
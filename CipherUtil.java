package cipherutil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CipherUtil {

    private final Cipher cipher;
    private final SecretKeySpec spec;
    
    public static class AES128{ 
    	private static CipherUtil cu;
    	public static String encrypt(String message){
        	try {
        		if(cu == null) cu = new CipherUtil(128, "AES","AES");
					return Base64.encodeBase64URLSafeString(cu.encrypt(message));
			} catch (InvalidKeyException e) {
			} catch (IllegalBlockSizeException e) {
			} catch (BadPaddingException e) {
    		} catch (NoSuchAlgorithmException e) {
    		} catch (NoSuchPaddingException e) {
    		}
			return null;
    	}
    	public static String decrypt(String msg){
        	try {
        		if(cu == null) cu = new CipherUtil(128, "AES","AES");
					return new String(cu.decrypt(Base64.decodeBase64(msg.getBytes())));
			} catch (InvalidKeyException e) {
			} catch (IllegalBlockSizeException e) {
			} catch (BadPaddingException e) {
    		} catch (NoSuchAlgorithmException e) {
    		} catch (NoSuchPaddingException e) {
    		}
			return null;
    	}    	
    }

    public CipherUtil(int keysize, String algorithm, String transformation)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
//    	  with auto generated key
//        KeyGenerator keygen = KeyGenerator.getInstance(algorithm);
//        keygen.init(keysize);
//        SecretKey skey = keygen.generateKey();
//        spec = new SecretKeySpec(skey.getEncoded(), algorithm);

        String _keyspec = "";
		try {
			byte[] key = "screatkey".getBytes("UTF-8");
	        MessageDigest sha = MessageDigest.getInstance("SHA-1");
	        key = sha.digest(key);
	        _keyspec = new String(Arrays.copyOf(key, 16));
		} catch (UnsupportedEncodingException e) {
		}

        spec = new SecretKeySpec(_keyspec.getBytes(), algorithm);
        cipher = Cipher.getInstance(transformation);
    }

    public byte[] decrypt(byte[] msg) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, spec);
        return cipher.doFinal(msg);
    }

    public byte[] encrypt(String msg) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        cipher.init(Cipher.ENCRYPT_MODE, spec);
        return cipher.doFinal(msg.getBytes());
    }

    public static String convertToHex(byte array[]) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if ((array[i] & 0xff) < 0x10)
                buffer.append("0");

            buffer.append(Integer.toString(array[i] & 0xff, 16));
        }
        return buffer.toString();
    }

    public static Set<String> getCryptImplementations(String serviceType) {
        Set<String> result = new HashSet<String>();

        for (Provider provider : Security.getProviders()) {
            Set<Object> keys = provider.keySet();
            for (Object k : keys) {
                String key = ((String) k).split(" ")[0];

                if (key.startsWith(serviceType + ".")) {
                    result.add(key.substring(serviceType.length() + 1));
                } else if (key.startsWith("Alg.Alias." + serviceType + ".")) {
                    result.add(key.substring(serviceType.length() + 11));
                }
            }
        }
        return result;
    }
 
    public static void printAvailableServices(String service) {
        boolean first = true;
        System.out.println("List of available " + service + "s");
        for (String s : getCryptImplementations(service)) {
            System.out.print(first ? s : ", " + s);
            first = false;
        }
    }
}


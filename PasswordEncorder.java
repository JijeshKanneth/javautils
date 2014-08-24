package security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class PasswordEncorder {
	public static String encodePassword(String rawPassword, String salt, int iterations){
		String saltedPass = mergePasswordAndSalt(rawPassword, salt);

        MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
	        byte[] digest = messageDigest.digest(saltedPass.getBytes("UTF-8"));

	        for (int i = 1; i < iterations; i++) {
	            digest = messageDigest.digest(digest);
	        }
	        
	        return  Base64.encodeBase64URLSafeString(digest);
		} catch (NoSuchAlgorithmException e1) {
		} catch (UnsupportedEncodingException e) {
		}
        return null;
	}
	
    private static String mergePasswordAndSalt(String password, Object salt) {
        if (password == null) {
            password = "";
        }

        if ((salt == null) || "".equals(salt)) {
            return password;
        } else {
            return password + "{" + salt.toString() + "}";
        }
    }	
}

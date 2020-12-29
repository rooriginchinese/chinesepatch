package unpack;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* renamed from: com.golitsyn.roo.ep.Hash */
public class Hash {
    public static String SHA1(byte[] bArr) {
        byte[] bArr2 = new byte[0];
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(bArr, 0, bArr.length);
            bArr2 = instance.digest();
        } catch (NoSuchAlgorithmException unused) {
        }
        return Convert.BytesToHex(bArr2);
    }
}

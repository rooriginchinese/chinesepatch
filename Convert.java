package unpack;

import java.nio.charset.StandardCharsets;

public class Convert {
    public static long Unsigned(byte b) {
        return ((long) b) & 255;
    }

    public static long Unsigned(int i) {
        return ((long) i) & 4294967295L;
    }

    public static long Unsigned(short s) {
        return ((long) s) & 65535;
    }

    public static byte[] IntToBytes(int i) {
        return IntToBytes(i, false);
    }

    public static byte[] IntToBytes(int i, boolean z) {
        byte[] bArr = new byte[4];
        for (int i2 = 0; i2 < 4; i2++) {
            bArr[z ? i2 : (4 - i2) - 1] = (byte) (i & 255);
            i >>= 8;
        }
        return bArr;
    }

    public static int BytesToInt(byte[] bArr) {
        return BytesToInt(bArr, false);
    }

    public static int BytesToInt(byte[] bArr, boolean z) {
        if (bArr == null || bArr.length != 4) {
            return 0;
        }
        int b = 0;
        for (int i = 0; i < 4; i++) {
            b = (b << 8) | (bArr[z ? (4 - i) - 1 : i] & 255);
        }
        return b;
    }

    public static byte[] LongToBytes(long j) {
        return LongToBytes(j, false);
    }

    public static byte[] LongToBytes(long j, boolean z) {
        byte[] bArr = new byte[8];
        for (int i = 0; i < 8; i++) {
            bArr[z ? i : (8 - i) - 1] = (byte) ((int) (255 & j));
            j >>= 8;
        }
        return bArr;
    }

    public static long BytesToLong(byte[] bArr) {
        return BytesToLong(bArr, false);
    }

    public static long BytesToLong(byte[] bArr, boolean z) {
        if (bArr == null || bArr.length != 8) {
            return 0;
        }
        long b = 0;
        for (int i = 0; i < 8; i++) {
            b = (b << 8) | (bArr[z ? (8 - i) - 1 : i] & 255);
        }
        return (long) b;
    }

    public static byte[] StringToBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String BytesToString(byte[] bArr) {
        return new String(bArr, StandardCharsets.UTF_8);
    }

    public static final byte[] HexToBytes(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static final String BytesToHex(byte[] bArr) {
        int length = bArr.length;
        char[] cArr = new char[(length * 2)];
        for (int i = 0; i < length; i++) {
            int b = bArr[i] & 255;
            int i2 = i * 2;
            cArr[i2] = Character.forDigit(b >>> 4, 16);
            cArr[i2 + 1] = Character.forDigit(b & 15, 16);
        }
        return new String(cArr);
    }

    public static byte[] ReverseBytes(byte[] bArr) {
        int length = bArr.length;
        for (int i = 0; i < length / 2; i++) {
            byte b = bArr[i];
            int i2 = (length - i) - 1;
            bArr[i] = bArr[i2];
            bArr[i2] = b;
        }
        return bArr;
    }
}


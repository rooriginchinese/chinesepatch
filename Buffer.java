package unpack;


import java.io.ByteArrayOutputStream;

public class Buffer {

    public static class Payload {
        byte[] bytes;
        int pos = 0;
    }

    public static String GetString(byte[] bArr, Payload payload) {
        ReadData(ReadLength(bArr, payload), bArr, payload);
        return Convert.BytesToString(payload.bytes);
    }

    public static int GetInt(byte[] bArr, Payload payload) {
        ReadData(4, bArr, payload);
        return Convert.BytesToInt(payload.bytes, true);
    }

    public static long GetLong(byte[] bArr, Payload payload) {
        ReadData(8, bArr, payload);
        return Convert.BytesToLong(payload.bytes, true);
    }

    public static int ReadLength(byte[] bArr, Payload payload) {
        int i = 0;
        int i2 = 0;
        while (payload.pos < bArr.length) {
//            byte b = bArr[payload.pos];
            long Unsigned = Convert.Unsigned(bArr[payload.pos]);
            i = (int) (((long) i) | ((Unsigned & 127) << i2));
            i2 += 7;
            payload.pos++;
            if (Unsigned <= 127) {
                break;
            }
        }
        return i;
    }

    public static byte[] EncodeLength(String str) {
        byte[] bArr = new byte[0];
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int length = Convert.StringToBytes(str).length;
            if (length >= 0 && length < 128) {
                byteArrayOutputStream.write(length);
            } else if (length > 0) {
                while (length > 0) {
                    byteArrayOutputStream.write((length & 127) | 128);
                    length >>= 7;
                }
            }
            byteArrayOutputStream.flush();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            int length2 = byteArray.length - 1;
            byteArray[length2] = (byte) (byteArray[length2] & Byte.MAX_VALUE);
            return byteArray;
        } catch (Exception e) {
            e.printStackTrace();
            return bArr;
        }
    }

    private static void ReadData(int i, byte[] bArr, Payload payload) {
        payload.bytes = new byte[0];
        if (payload.pos + i <= bArr.length) {
            payload.bytes = new byte[i];
            System.arraycopy(bArr, payload.pos, payload.bytes, 0, i);
        }
        payload.pos += i;
    }
}


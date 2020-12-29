package unpack;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/* renamed from: com.golitsyn.roo.ep.Web */
public class Web {
    private static final String charset = "UTF-8";

    public static String Encode(LinkedHashMap<String, String> linkedHashMap) {
        StringJoiner stringJoiner = new StringJoiner("&");
        try {
            for (Map.Entry next : linkedHashMap.entrySet()) {
                stringJoiner.add(URLEncoder.encode((String) next.getKey(), charset) + "=" + URLEncoder.encode((String) next.getValue(), charset));
            }
        } catch (UnsupportedEncodingException unused) {
        }
        return stringJoiner.toString();
    }

    public static byte[] Get(String str) {
        return Get(str, new LinkedHashMap());
    }

    public static byte[] Get(String str, LinkedHashMap<String, String> linkedHashMap) {
        byte[] bArr;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(30000);
            if (linkedHashMap.isEmpty()) {
                httpURLConnection.setRequestMethod("GET");
            } else {
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, charset));
                bufferedWriter.write(Encode(linkedHashMap));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
            }
            if (httpURLConnection.getResponseCode() == 200) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                byte[] bArr2 = new byte[8192];
                while (true) {
                    int read = bufferedInputStream.read(bArr2);
                    if (-1 == read) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr2, 0, read);
                }
                byteArrayOutputStream.close();
                bufferedInputStream.close();
                bArr = byteArrayOutputStream.toByteArray();
            } else {
                bArr = new byte[0];
            }
            return bArr;
        } catch (Exception e) {
            return new byte[0];
        }
    }
}

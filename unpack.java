package unpack;
import java.io.*;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;

public class unpack {

	public static void main(String[] args) {
		try {
			
			String fileName = "890358105.robytes";
//			ko text
			File f = new File("C:\\Users\\username\\eclipse-workspace\\unpack\\src\\unpack\\chinesepatch\\input files\\korean\\".concat(fileName));
			byte[] bArr = Files.readAllBytes(f.toPath());
			Database DatabaseUnpackKo = DatabaseUnpack(bArr);
			LinkedHashMap<Long, String> rootMap = DatabaseToRoot(DatabaseUnpackKo.items);
			
//			ch text
			File fCh = new File("C:\\Users\\username\\eclipse-workspace\\unpack\\src\\unpack\\chinesepatch\\input files\\simp\\".concat(fileName));
			byte[] bArrCh = Files.readAllBytes(fCh.toPath());
			Database DatabaseUnpackCh = DatabaseUnpack(bArrCh);

			LinkedHashMap<Long, String> chMap = DatabaseToRoot(DatabaseUnpackCh.items);

//			en text
			byte[] enArr = Web.Get("http://roo.golitsyn.com/translations/json/EN");
			JSONObject jSONObject = new JSONObject(Convert.BytesToString(enArr));
            LinkedHashMap enMap = new LinkedHashMap();
            JSONArray names = jSONObject.names();
            for (int i = 0; i < names.length(); i++) {
                String string = names.getString(i);
                String string2 = jSONObject.getString(string);
                DatabaseItem databaseItem = new DatabaseItem();
                databaseItem.f88id = new Long(string).longValue();
                databaseItem.text = string2;
                if (!enMap.containsKey(Long.valueOf(databaseItem.f88id))) {
                	enMap.put(Long.valueOf(databaseItem.f88id), databaseItem.text);
                }
            }
			
            for (Map.Entry<Long, String> entry : rootMap.entrySet()) {
                Long key = entry.getKey();
                String value = entry.getValue();
                if(chMap.containsKey(key)) {
                	rootMap.put(key, ZhConverterUtil.toTraditional(chMap.get(key)));
                }else if(enMap.containsKey(key)) {
                	rootMap.put(key, enMap.get(key).toString());
                }
            }
            DatabasePatchItems(DatabaseUnpackKo, rootMap);
            byte[] DatabasePack = DatabasePack(DatabaseUnpackKo);
            WriteFile("C:\\Users\\username\\eclipse-workspace\\unpack\\src\\unpack\\chinesepatch\\output files\\".concat(fileName), DatabasePack);
            

            byte[] ChecksumRead = ChecksumRead();
            Checksum ChecksumUnpack = ChecksumUnpack(ChecksumRead);
            ChecksumPatch(ChecksumUnpack, (long) DatabasePack.length, Hash.SHA1(DatabasePack), fileName);
            WriteFile("C:\\Users\\username\\eclipse-workspace\\unpack\\src\\unpack\\chinesepatch\\output files\\__file__list__", ChecksumPack(ChecksumUnpack));
            
			System.out.println("Done");
		}
		catch(Exception e) {

		}
	}

	public static class DatabaseItem {

		/* renamed from: id */
		long f88id;
		String text;
	}

	public static class Database {
		int count;
		LinkedHashMap<Long, DatabaseItem> items = new LinkedHashMap<>();
		int unknown1;
		int unknown2;
	}

	public static class Checksum {
        int count;
        long filesize;
        LinkedHashMap<String, ChecksumItem> items = new LinkedHashMap<>();
    }

    public static class ChecksumItem {
        long filesize;
        String hash;
        String name;
    }

	public static Database DatabaseUnpack(byte[] bArr) {
		Database database = new Database();
		if (bArr.length == 0) {
			return database;
		}
		Buffer.Payload payload = new Buffer.Payload();
		database.count = Buffer.GetInt(bArr, payload);
		for (int i = 0; i < database.count; i++) {
			DatabaseItem databaseItem = new DatabaseItem();
			databaseItem.f88id = Convert.Unsigned(Buffer.GetInt(bArr, payload));
			databaseItem.text = Buffer.GetString(bArr, payload);
			database.items.put(Long.valueOf(databaseItem.f88id), databaseItem);
		}
		database.unknown1 = Buffer.GetInt(bArr, payload);
		database.unknown2 = Buffer.GetInt(bArr, payload);
		return database;
	}

	public static LinkedHashMap<Long, String> DatabaseToRoot(LinkedHashMap<Long, DatabaseItem> linkedHashMap) {
		LinkedHashMap<Long, String> rootMap = new LinkedHashMap<>();
		for (Map.Entry next : linkedHashMap.entrySet()) {
			Long l = (Long) next.getKey();
			DatabaseItem databaseItem = (DatabaseItem) next.getValue();
			rootMap.put(l,databaseItem.text);
		}
		return rootMap;
	}
	
    public static void DatabasePatchItems(Database database, LinkedHashMap<Long, String> linkedHashMap) {
        for (Map.Entry next : linkedHashMap.entrySet()) {
            Long l = (Long) next.getKey();
            String value = (String) next.getValue();
            if (database.items.containsKey(l)) {
                database.items.get(l).text = value;
            }
        }
    }

    public static byte[] DatabasePack(Database database) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(Convert.IntToBytes(database.items.size(), true));
            for (DatabaseItem next : database.items.values()) {
                String str = next.text;
                byteArrayOutputStream.write(Convert.IntToBytes((int) next.f88id, true));
                byteArrayOutputStream.write(Buffer.EncodeLength(str));
                byteArrayOutputStream.write(Convert.StringToBytes(str));
            }
            byteArrayOutputStream.write(Convert.IntToBytes(database.unknown1, true));
            byteArrayOutputStream.write(Convert.IntToBytes(database.unknown2, true));
            byteArrayOutputStream.flush();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return byteArray;
        } catch (IOException unused) {
            return new byte[0];
        }
    }
    
    public static void WriteFile(String str, byte[] bArr) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str, false);
            fileOutputStream.write(bArr);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException unused) {
        }
    }
    
    public static byte[] ChecksumRead() {
    	String str = "C:\\Users\\username\\eclipse-workspace\\unpack\\src\\unpack\\chinesepatch\\input files\\__file__list__";
    	byte[] bArr = new byte[0];
        File file = new File(str);
        if (!file.exists()) {
            return bArr;
        }
        try {
            byte[] bArr2 = new byte[((int) file.length())];
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            dataInputStream.readFully(bArr2);
            dataInputStream.close();
            return bArr2;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return bArr;
        } catch (IOException e2) {
            e2.printStackTrace();
            return bArr;
        }
    }
    
    public static Checksum ChecksumUnpack(byte[] bArr) {
        Checksum checksum = new Checksum();
        if (bArr.length == 0) {
            return checksum;
        }
        Buffer.Payload payload = new Buffer.Payload();
        checksum.filesize = Buffer.GetLong(bArr, payload);
        checksum.count = Buffer.GetInt(bArr, payload);
        for (int i = 0; i < checksum.count; i++) {
            ChecksumItem checksumItem = new ChecksumItem();
            checksumItem.name = Buffer.GetString(bArr, payload);
            checksumItem.filesize = Buffer.GetLong(bArr, payload);
            checksumItem.hash = Buffer.GetString(bArr, payload);
            checksum.items.put(checksumItem.name, checksumItem);
        }
        return checksum;
    }
    
    public static void ChecksumPatch(Checksum checksum, long j, String str, String DATABASE) {
        if (checksum.items.containsKey(DATABASE)) {
            ChecksumItem checksumItem = checksum.items.get(DATABASE);
            checksumItem.filesize = j;
            checksumItem.hash = str;
        }
    }
    
    public static byte[] ChecksumPack(Checksum checksum) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(Convert.LongToBytes(checksum.filesize, true));
            byteArrayOutputStream.write(Convert.IntToBytes(checksum.items.size(), true));
            for (ChecksumItem next : checksum.items.values()) {
                byteArrayOutputStream.write(Buffer.EncodeLength(next.name));
                byteArrayOutputStream.write(Convert.StringToBytes(next.name));
                byteArrayOutputStream.write(Convert.LongToBytes(next.filesize, true));
                byteArrayOutputStream.write(Buffer.EncodeLength(next.hash));
                byteArrayOutputStream.write(Convert.StringToBytes(next.hash));
            }
            byteArrayOutputStream.flush();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return byteArray;
        } catch (IOException unused) {
            return new byte[0];
        }
    }

}

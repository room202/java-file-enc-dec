import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class Main {

    // 鍵生成（同じキーで暗号化・復号化）
    public static SecretKey generateKey(String password) throws Exception {
        // パスワードからAESキーを生成
        byte[] keyBytes = password.getBytes("UTF-8");
        // AESは128bit、192bit、256bitの鍵長をサポート
        byte[] key = new byte[16]; // AES 128bit
        // 鍵長に合わせてパスワードを切り詰める
        System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, key.length));
        return new SecretKeySpec(key, "AES");
    }

    // ファイルの暗号化
    public static void encryptFile(String inputFile, String outputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        try (FileInputStream fis = new FileInputStream(inputFile);
                FileOutputStream fos = new FileOutputStream(outputFile);
                CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, read);
            }
        }

        System.out.println("■ 暗号化完了: " + outputFile);
    }

    // ファイルの復号化
    public static void decryptFile(String inputFile, String outputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        try (FileInputStream fis = new FileInputStream(inputFile);
                CipherInputStream cis = new CipherInputStream(fis, cipher);
                FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        }

        System.out.println("■ 復号化完了: " + outputFile);
    }

    public static void main(String[] args) {
        try {
            String password = "mysecretkey12345"; // パスワード（鍵の元）
            SecretKey key = generateKey(password);

            // ファイル名
            String originalFile = "target\\target1.txt";
            String encryptedFile = "暗号化\\target1.txt.enc";
            String decryptedFile = "復号化\\target1_decrypted.txt";

            encryptFile(originalFile, encryptedFile, key);
            decryptFile(encryptedFile, decryptedFile, key);

            // ファイル名
            originalFile = "target\\onepiece01_luffy2.png";
            encryptedFile = "暗号化\\onepiece01_luffy2.png.enc";
            decryptedFile = "復号化\\onepiece01_luffy2_decrypted.png";

            encryptFile(originalFile, encryptedFile, key);
            decryptFile(encryptedFile, decryptedFile, key);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author wyl
 * @description 加解密工具类
 * @date 2109/05/17
 * @version 1.0.0
 */
public class AESUtils {
    private static final String defaultCharset = "UTF-8";
    private static final String KEY_AES = "AES";
    private static RSA rsa;
    private static AES aes;

    static {
        rsa = new RSA();
        aes = new AES();
    }
    private AESUtils(){

    }
    /**
     * 单例操作（静态内部类方式）
     */
    private static class SingletonHolder {
        private  static AESUtils instance = new AESUtils();

    }
    public static AESUtils getInstance() {
        return SingletonHolder.instance;
    }

    public static KeyPair getKeyPair() {
        return rsa.getKeyPair();

    }
    public static String getPublicKey(KeyPair keyPair) {
        return rsa.getPublicKey(keyPair);
    }
    public static String getPrivateKey(KeyPair keyPair) {
        return rsa.getPrivateKey(keyPair);
    }
    public static PrivateKey string2PrivateKey(String priStr) {
        return rsa.string2PrivateKey(priStr);
    }

    public static PublicKey string2PublicKey(String pubStr){
        return rsa.string2PublicKey(pubStr);
    }
    public static String publicEncrypt(String data, PublicKey publicKey) {
        return rsa.publicEncrypt(data, publicKey);
    }
    public static  String privateDecrypt(String data, PrivateKey privateKey){
        return rsa.privateDecrypt(data, privateKey);
    }

    public static String encrypt(String data, String key) {
        return aes.encrypt(data, key);
    }

    public static String decrypt(String data, String key) {
        return aes.decrypt(data, key);
    }

    /**
     * @author wyl
     * rsa 非对称加密 用于加密AES密钥
     */
    private static class RSA {
        /**
         * 生成公钥私钥秘钥对
         * @return
         */
        public  KeyPair getKeyPair(){
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                return keyPair;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 获取公钥(十六进制编码)
         * @param keyPair
         * @return
         */
        public  String getPublicKey(KeyPair keyPair) {
            PublicKey publicKey = keyPair.getPublic();
            byte[] bytes = publicKey.getEncoded();
            return byte2Hex(bytes);
        }

        /**
         * 获取私钥(十六进制编码)
         * @param keyPair
         * @return
         */
        public  String getPrivateKey(KeyPair keyPair) {
            PrivateKey privateKey = keyPair.getPrivate();
            byte[] bytes = privateKey.getEncoded();
            return byte2Hex(bytes);
        }

        /**
         * 将十六进制编码后的公钥转换成PublicKey对象
         * @param pubStr
         * @return
         */
        public  PublicKey string2PublicKey(String pubStr){
            try {
                byte[] keyBytes = hex2Bytes(pubStr);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(keySpec);
                return publicKey;
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * /将十六进制编码后的私钥转换成PrivateKey对象
         * @param priStr
         * @return
         */
        public  PrivateKey string2PrivateKey(String priStr){
            try {
                byte[] keyBytes = hex2Bytes(priStr);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
                return privateKey;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 公钥加密
         * @param data
         * @param publicKey
         * @return
         */
        public  String publicEncrypt(String data, PublicKey publicKey) {
            try {
                byte[] content = data.getBytes();
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] bytes = cipher.doFinal(content);
                return byte2Hex(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 私钥解密
         * @param data
         * @param privateKey
         * @return
         */
        private   String privateDecrypt(String data, PrivateKey privateKey){
            try {
                byte[] content = hex2Bytes(data);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] bytes = cipher.doFinal(content);
                return new String(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * @author wyl
     * aes 对称加密内部类  用于加密传输的数据
     */
    private static class AES {
        /**
         * 加密
         * @param data 需要加密的内容
         * @param key  加密密钥
         * @return
         */
        private   String encrypt(String data, String key) {
            return doAES(data, key, Cipher.ENCRYPT_MODE);
        }

        private   String decrypt(String data, String key) {
            return doAES(data, key, Cipher.DECRYPT_MODE);
        }

        private  String doAES(String data, String key, int encryptMode) {
            try {
                if (!CommonUtils.isNotBlank(data)) {
                    return null;
                }
                byte[] content;
                if (encryptMode == Cipher.ENCRYPT_MODE) {
                    content = data.getBytes(defaultCharset);
                } else {
                    content = hex2Bytes(data);
                }
                //1.构造密钥生成器，指定为AES算法,不区分大小写
                KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
                //2.根据ecnodeRules规则初始化密钥生成器
                //生成一个128位的随机源,根据传入的字节数组
                kgen.init(128, new SecureRandom(key.getBytes()));
                //3.产生原始对称密钥
                SecretKey secretKey = kgen.generateKey();
                //4.获得原始对称密钥的字节数组
                byte[] encodeFormat = secretKey.getEncoded();
                //5.根据字节数据生成AES密钥
                SecretKeySpec keySpec = new SecretKeySpec(encodeFormat, KEY_AES);
                //SecretKeySpec keySpec = new SecretKeySpec(md5Digest.digest(key.getBytes(defaultCharset)), KEY_AES);
                //6.根据指定算法AES生成密码器
                Cipher cipher = Cipher.getInstance(KEY_AES);
                //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KE
                cipher.init(encryptMode, keySpec);
                byte[] result = cipher.doFinal(content);
                if (encryptMode == Cipher.ENCRYPT_MODE) {
                    // 将二进制转换成16进制
                    return byte2Hex(result);
                } else {
                    return new String(result, defaultCharset);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    /**
     * 将二进制转换成16进制
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        if (!CommonUtils.isNotBlank(bytes)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i ++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] hex2Bytes(String hexStr) {
        if (!CommonUtils.isNotBlank(hexStr)){
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}

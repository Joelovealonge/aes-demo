import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Main {

    public static void main(String[] args) {
        System.out.println("=============AES加密");
        String data = "你好哈二胖子！";
        System.out.println("====加密前====" + data);
        // AES加密密钥 应该是随机的(这儿写死 为了方便)
        String key = "helloddd";
        // 直接把公钥传给客户端
        //===============生成公钥和私钥，公钥传给客户端，私钥服务端保留==================
        //生成RSA公钥和私钥，并十六进制编码
        KeyPair keyPair = AESUtils.getKeyPair();
        String publicKeyStr = AESUtils.getPublicKey(keyPair);
        String privateKeyStr = AESUtils.getPrivateKey(keyPair);
        System.out.println("RSA公钥十六进制编码:" + publicKeyStr);
        System.out.println("RSA私钥十六进制编码:" + privateKeyStr);

        //=================客户端=================

        //将Base64编码后的公钥转换成PublicKey对象
        PublicKey publicKey = AESUtils.string2PublicKey(publicKeyStr);
        //用公钥加密
        String publicEncrypt = AESUtils.publicEncrypt(key, publicKey);

        System.out.println("AES加密密钥使用公钥加密并十六进制编码的结果：" + publicEncrypt);

        String encrypt = AESUtils.encrypt(data, key);
        System.out.println("====加密后====" + encrypt);


        //##############	网络上传输的内容有Base64编码后的公钥 和 十六进制编码后的公钥加密的内容   以及AES加密后的数据  #################


        //===================服务端================
        //将Base64编码后的私钥转换成PrivateKey对象
        PrivateKey privateKey = AESUtils.string2PrivateKey(privateKeyStr);

        //AES加密密钥用私钥解密
        String privateDecrypt = AESUtils.privateDecrypt(publicEncrypt, privateKey);
        //解密后的明文
        System.out.println("AES密钥明文: " + privateDecrypt);




        String decrypt = AESUtils.decrypt(encrypt, new String(privateDecrypt));
        System.out.println("====解密后====" + decrypt);

    }
}

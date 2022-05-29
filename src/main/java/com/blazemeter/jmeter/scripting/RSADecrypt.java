package com.blazemeter.jmeter.scripting;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RSADecrypt extends AbstractJavaSamplerClient {

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();
        try {
            byte[] encryptedKey = Base64.getDecoder()
                    .decode(javaSamplerContext.getJMeterVariables().get("encryptedKey"));
            byte[] encryptedData = Base64.getDecoder()
                    .decode(javaSamplerContext.getJMeterVariables().get("encryptedData"));

            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder()
                    .decode(javaSamplerContext.getJMeterVariables().get("privateKey")));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.PRIVATE_KEY, privateKey);
            byte[] decryptedKey = cipher.doFinal(encryptedKey);

            SecretKey originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] decryptedData = aesCipher.doFinal(encryptedData);
            javaSamplerContext.getJMeterVariables().put("decryptedData", new String(decryptedData));
            sampleResult.setSuccessful(true);
            sampleResult.setResponseCodeOK();
            sampleResult.setResponseData("Decrypted data: "
                    + javaSamplerContext.getJMeterVariables().get("decryptedData"), "UTF-8");

        } catch (Exception ex) {
            sampleResult.setSuccessful(false);
            sampleResult.setResponseCode("500");
            sampleResult.setResponseMessage(ex.getMessage());
        }
        sampleResult.sampleEnd();
        return sampleResult;
    }
}

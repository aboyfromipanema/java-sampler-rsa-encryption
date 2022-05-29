package com.blazemeter.jmeter.scripting;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAEncrypt extends AbstractJavaSamplerClient {

    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("textToEncrypt", "${textToEncrypt}");
        return params;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();
        try {
            String Parameters = javaSamplerContext.getParameter("textToEncrypt");
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = aesCipher.doFinal(Parameters.getBytes());

            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder()
                    .decode(javaSamplerContext.getJMeterVariables().get("publicKey")));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.PUBLIC_KEY, publicKey);
            byte[] encryptedKey = cipher.doFinal(secretKey.getEncoded());


            String key = Base64.getEncoder().encodeToString(encryptedKey);
            String data = Base64.getEncoder().encodeToString(encryptedData);
            javaSamplerContext.getJMeterVariables().put("encryptedKey", key);
            javaSamplerContext.getJMeterVariables().put("encryptedData", data);

            sampleResult.setSuccessful(true);
            sampleResult.setResponseCodeOK();
            sampleResult.setResponseData("Encrypted key: " + key + "\nEncrypted data: " + data, "UTF-8");
        } catch (Exception ex) {
            sampleResult.setSuccessful(false);
            sampleResult.setResponseCode("500");
            sampleResult.setResponseMessage(ex.getMessage());
        }
        sampleResult.sampleEnd();
        return sampleResult;
    }
}

# java-sampler-rsa-encryption
Apache JMeter's Java Request sampler which does AES/RSA text encryption

## Installation 

1. Use `mvn package` command to build the plugin
2. Copy `java-sampler-rsa-encryption-1.0-SNAPSHOT.jar` file from _target_ folder to "lib/ext" folder of your JMeter 
installation
3. Restart JMeter if its running
4. Add Java Request sampler to Test Plan
5. `com.blazemeter.jmeter.scripting.RSAEncrypt` and `com.blazemeter.jmeter.scripting.RSADecrypt` classes should be 
6. awailable
 
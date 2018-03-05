import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.SigningAlgorithm;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;

public class AuthUtil extends AWS4Signer {
    private String serviceName;
    private AWSCredentials credentials;
    private String region;

    public AuthUtil(AWSCredentials credentials, String region, String serviceName) {
        this.credentials = credentials;
        this.region = region;
        this.serviceName = serviceName;
    }

    public String getSignature(String policy, LocalDateTime dateTime) {
        try {
            String dateStamp = dateTime.format(ofPattern("yyyyMMdd"));
            return Hex.encodeHexString(hmacSha256(newSigningKey(credentials, dateStamp, region, serviceName), policy));
        } catch (Exception e) {
            throw new RuntimeException("Error", e);
        }
    }

    private byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance(SigningAlgorithm.HmacSHA256.name());
        mac.init(new SecretKeySpec(key, SigningAlgorithm.HmacSHA256.name()));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

}

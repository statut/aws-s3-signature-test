import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;

public class HttpEntityUtil {
    private String policy;
    private String accessKey;
    private String region;
    private String algorithm = "AWS4-HMAC-SHA256";

    public HttpEntityUtil(String policy, String accessKey, String region) {
        this.policy = policy;
        this.accessKey = accessKey;
        this.region = region;
    }

    public HttpEntity buildPostMultipartDataEntity(String objectKey, byte[] data, String signature, LocalDateTime dateTime) {

        String dateTimeStr = dateTime.format(ofPattern("yyyyMMdd'T'HHmmss'Z'"));
        String date = dateTime.format(ofPattern("yyyyMMdd"));

        return MultipartEntityBuilder
            .create()
            .addTextBody("key", objectKey)
            .addTextBody("Policy", policy)
            .addTextBody("X-Amz-Signature", signature)
            .addTextBody("X-Amz-Algorithm", algorithm)
            .addTextBody("X-Amz-Date", dateTimeStr)
            .addTextBody("X-Amz-Credential", String.format("%s/%s/%s/s3/aws4_request", accessKey, date, region))
            .addBinaryBody("file", data)
            .build();
    }
}

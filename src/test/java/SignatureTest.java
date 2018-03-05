import com.amazonaws.auth.BasicAWSCredentials;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.testng.Assert.assertEquals;

@Test
public class SignatureTest {
    private String accessKey = "SOME KEY";
    private String secretKey = "SOME SECRET";
    private String region = "us-west-1";
    private String service = "s3";
    private String bucket = "sample-cf";

    private String policy;
    private AuthUtil authUtil;
    private HttpEntityUtil httpEntityUtil;

    @BeforeClass
    public void setUp() throws IOException {
        policy = readPolicy();
        authUtil = new AuthUtil(new BasicAWSCredentials(accessKey, secretKey), region, service);
        httpEntityUtil = new HttpEntityUtil(policy, accessKey, region);
    }

    @Test
    public void testSignature() throws IOException {
        LocalDateTime localDateTime = LocalDateTime.now();
        String signature = authUtil.getSignature(policy, localDateTime);

        HttpEntity httpEntity = httpEntityUtil.buildPostMultipartDataEntity("test-object.txt", "Test data".getBytes(),
            signature, localDateTime);

        Response response = Request.Post(String.format("http://%s.s3.amazonaws.com", bucket))
            .body(httpEntity)
            .execute();

        assertEquals(response.returnResponse().getStatusLine().getStatusCode(), 204);
    }

    private String readPolicy() throws IOException {
        String policy = StreamUtils.copyToString((new ClassPathResource("policy.txt")).getInputStream(), UTF_8);
        return Base64.getEncoder().encodeToString(policy.getBytes("UTF8"));
    }
}

import com.lyheden.thoughtworks.go.plugin.StatsDNotificationPluginImpl;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.timgroup.statsd.StatsDClient;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;

/**
 * Created by johan on 28/06/15.
 */
public class StatsDNotificationPluginImplTest {

    @InjectMocks private StatsDNotificationPluginImpl plugin = new StatsDNotificationPluginImpl();
    @Mock private StatsDClient statsDClient;
    private String stageStatusChangeJson1;
    private String stageStatusChangeJson2;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.doNothing().when(statsDClient).increment(anyString());
        Mockito.doNothing().when(statsDClient).recordExecutionTime(anyString(), anyLong());
        stageStatusChangeJson1 = IOUtils.toString(this.getClass().getResourceAsStream("stage-status-change-1.json"), "UTF-8");
        stageStatusChangeJson2 = IOUtils.toString(this.getClass().getResourceAsStream("stage-status-change-2.json"), "UTF-8");
    }

    @Test
    public void testParsingStatusCompleted() throws UnhandledRequestTypeException {
        GoPluginApiResponse response = plugin.handle(generateFakeRequest(stageStatusChangeJson1));
        assertNotNull(response);
        assertEquals(response.responseCode(), 200);
        String responseBody = response.responseBody();
        assertTrue(responseBody.contains("Recorded execution time key pipeline-name.stage-name.job.job-name.time value 46000"));
    }

    @Test
    public void testParsingStatusBuilding() throws UnhandledRequestTypeException {
        GoPluginApiResponse response = plugin.handle(generateFakeRequest(stageStatusChangeJson2));
        assertNotNull(response);
        assertEquals(response.responseCode(), 200);
    }

    private GoPluginApiRequest generateFakeRequest(final String rb) {
        return new GoPluginApiRequest() {
            @Override
            public String extension() {
                return null;
            }

            @Override
            public String extensionVersion() {
                return null;
            }

            @Override
            public String requestName() {
                return "stage-status";
            }

            @Override
            public Map<String, String> requestParameters() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                return null;
            }

            @Override
            public String requestBody() {
                return rb;
            }
        };
    }

}
package ca.uhn.fhir.batch2.jobs.imprt;

import ca.uhn.fhir.test.utilities.server.HttpServletExtension;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BulkImportFileServletTest {

	private BulkImportFileServlet mySvc = new BulkImportFileServlet();

	static final String ourInput = "{\"resourceType\":\"Patient\", \"id\": \"A\", \"active\": true}\n" +
		"{\"resourceType\":\"Patient\", \"id\": \"B\", \"active\": false}";

	@RegisterExtension
	private HttpServletExtension myServletExtension = new HttpServletExtension()
		.withServlet(mySvc)
		.withContextPath("/context")
		.withServletPath("/base/path/*");

	@BeforeEach
	public void beforeEach() {
		mySvc.clearFiles();
	}

	@Test
	public void testDownloadFile() throws IOException {

		String index = mySvc.registerFileByContents(ourInput);

		String url = myServletExtension.getBaseUrl() + "/download?index=" + index;

		executeBulkImportAndCheckReturnedContentType(url);

	}


	private void executeBulkImportAndCheckReturnedContentType(String theUrl)  throws IOException{
		CloseableHttpClient client = myServletExtension.getHttpClient();

		try (CloseableHttpResponse response = client.execute(new HttpGet(theUrl))) {
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			String responseHeaderContentType = response.getFirstHeader("content-type").getValue();

			assertEquals(200, response.getStatusLine().getStatusCode());
			assertEquals(BulkImportFileServlet.DEFAULT_HEADER_CONTENT_TYPE, responseHeaderContentType);
			assertEquals(ourInput, responseBody);
		}
	}


	@Test
	public void testInvalidRequests() throws IOException {
		CloseableHttpClient client = myServletExtension.getHttpClient();

		String url;

		url = myServletExtension.getBaseUrl() + "/blah";
		try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			assertEquals(404, response.getStatusLine().getStatusCode());
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			assertEquals("Failed to handle response. See server logs for details.", responseBody);
		}

		url = "http://localhost:" + myServletExtension.getPort() + "/context/base/path/foo";
		try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			assertEquals(404, response.getStatusLine().getStatusCode());
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			assertEquals("Failed to handle response. See server logs for details.", responseBody);
		}

		url = myServletExtension.getBaseUrl() + "/download";
		try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			assertEquals(404, response.getStatusLine().getStatusCode());
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			assertEquals("Failed to handle response. See server logs for details.", responseBody);
		}

		url = myServletExtension.getBaseUrl() + "/download?";
		try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			assertEquals(404, response.getStatusLine().getStatusCode());
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			assertEquals("Failed to handle response. See server logs for details.", responseBody);
		}

		url = myServletExtension.getBaseUrl() + "/download?index=";
		try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			assertEquals(404, response.getStatusLine().getStatusCode());
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			assertEquals("Failed to handle response. See server logs for details.", responseBody);
		}

		url = myServletExtension.getBaseUrl() + "/download?index=A";
		try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			assertEquals(404, response.getStatusLine().getStatusCode());
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			assertEquals("Failed to handle response. See server logs for details.", responseBody);
		}

		url = myServletExtension.getBaseUrl() + "/download?index=22";
		try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			assertEquals(404, response.getStatusLine().getStatusCode());
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			assertEquals("Failed to handle response. See server logs for details.", responseBody);
		}

	}

}

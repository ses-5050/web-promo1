package satasme.promo.web.controller;

/**
 * Sample Java code for youtube.videos.insert
 * See instructions for running these code samples locally:
 * https://developers.google.com/explorer-help/guides/code_samples#java
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import satasme.promo.web.service.FilesStorageService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ytvideo")
public class YoutubeController {
	@Autowired
	private FilesStorageService storageService;

	private static final String CLIENT_SECRETS = "client_secret.json";
	private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.upload");

	private static final String APPLICATION_NAME = "API code samples";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/**
	 * Create an authorized Credential object.
	 *
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
		// Load client secrets.
		InputStream in = YoutubeController.class.getResourceAsStream(CLIENT_SECRETS);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		System.out.println("ooooooooooooo");
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, SCOPES).build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		return credential;
	}

	/**
	 * Build and return an authorized API client service.
	 *
	 * @return an authorized API client service
	 * @throws GeneralSecurityException, IOException
	 */
	public static YouTube getService() throws GeneralSecurityException, IOException {
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		Credential credential = authorize(httpTransport);
		return new YouTube.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
				.build();
	}

	/**
	 * Call function to create API service object. Define and execute API request.
	 * Print API response.
	 *
	 * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
	 */
	@GetMapping("/{uvid}")
	public void upload(@PathVariable(value = "uvid") String uvid) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
		YouTube youtubeService = getService();
		// Define the Video object, which will be uploaded as the request body.
		Video video = new Video();

		// Add the snippet object property to the Video object.
		VideoSnippet snippet = new VideoSnippet();
		video.setSnippet(snippet);
		
		// Add the status object property to the Video object.
        VideoStatus status = new VideoStatus();
        status.setUploadStatus("uploaded");
        video.setStatus(status);

		// TODO: For this request to work, you must replace "YOUR_FILE"
		// with a pointer to the actual file you are uploading.
		// The maximum file size for this operation is 137438953472.
		File mediaFile = new File("YOUR_FILE");
		Resource load = storageService.load("video 3.mp4", "70", "1hruc8cc0gshe");
		File myfile = load.getFile();
		System.out.println(myfile);
		InputStreamContent mediaContent = new InputStreamContent("application/octet-stream",
				new BufferedInputStream(new FileInputStream(myfile)));
		mediaContent.setLength(mediaFile.length());

		// Define and execute the API request
		YouTube.Videos.Insert request = youtubeService.videos().insert("snippet, status", video, mediaContent);
		Video response = request.execute();
		System.out.println(response);
	}
	
	@DeleteMapping("/{uvid}")
	public void deletevideo(@PathVariable(value = "uvid") String uvid) {
		System.out.println("aaaaaaaaaaaaaaaaaa");
	}
}

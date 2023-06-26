package vn.edu.clevai.bplog.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

@SuppressWarnings("deprecation")
public class GoogleAuthorizationUtilities {

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    /**
     * Creates an authorized Credential object.
     *
     * @param netHttpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(final NetHttpTransport netHttpTransport) throws IOException {
        final String TOKENS_DIRECTORY_PATH = Paths.get("tokens").toString();

        FileDataStoreFactory tokenStore = new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH));

        InputStream tokenStream = GoogleAuthorizationUtilities.class
            .getResourceAsStream("/tokens/StoredCredential");
        if (tokenStream == null) {
            throw new FileNotFoundException("Resource not found: /tokens/StoredCredential");
        }
        FileUtils.copyInputStreamToFile(tokenStream, new File(tokenStore
            .getDataDirectory().getAbsolutePath(), "/StoredCredential"));

        // Load client secrets.
        InputStream in = GoogleAuthorizationUtilities.class.getResourceAsStream("/credentials/credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Resource not found: /credentials/credentials.json");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
            .Builder(netHttpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(tokenStore)
            .setAccessType("offline")
            .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8094).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}

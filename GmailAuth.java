package com.expensetracker.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Handles OAuth2 login for Gmail using the downloaded credentials.json file.
 * Run this once. It opens a browser window for you to approve access,
 * then stores a token locally so you don't have to log in every time.
 */
public class GmailAuth {

    private static final String APPLICATION_NAME = "Personal Expense Tracker";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    // Read-only access to Gmail is enough for importing transactions
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);

    // Path to your renamed credentials file (the one you downloaded from Google Cloud)
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static Credential getCredentials(final com.google.api.client.http.javanet.NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GmailAuth.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new IOException("Resource not found: " + CREDENTIALS_FILE_PATH
                    + ". Put your renamed credentials.json in src/main/resources/");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new java.io.InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static com.google.api.services.gmail.Gmail getGmailService() throws IOException, GeneralSecurityException {
        final com.google.api.client.http.javanet.NetHttpTransport HTTP_TRANSPORT =
                GoogleNetHttpTransport.newTrustedTransport();
        return new com.google.api.services.gmail.Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import dtd.DTD;
import dtd.Field;
import dtd.Section;
import dtd.TypeSpecificElement;
import sheets.MetadataSheetParser;

public class GenerateDTD {
    private static final String APPLICATION_NAME = "KPMP Convert Metadata Sheet to DTD ";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GenerateDTD.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1R40Kl00sr1WskdiD7lXgEaG81jkkwH8xiSxEg0LSCFY";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        DTD dtd = new DTD();
        dtd.setVersion("1.0");
        MetadataSheetParser parser = new MetadataSheetParser(service, spreadsheetId);
        List<Field> standardFields = parser.getStandardFields();
        Section standardFieldSection = new Section();
        standardFieldSection.setSectionHeader(standardFields.get(0).getSectionName());
        standardFieldSection.setFields(standardFields);
        dtd.setStandardFields(standardFieldSection);
        Map<String, TypeSpecificElement> typeSpecificElements = parser.getTypeSpecificElements();
        List<String> dataTypes = parser.convertTypeSpecificElementsToDataTypes(typeSpecificElements);
        List<Field> fields = parser.getAllFields(dataTypes);
        parser.populateTypeSpecificElements(typeSpecificElements, fields);
        dtd.setTypeSpecificElements(typeSpecificElements);
        File file = new File("metadataDTD.json");
        FileWriter fstream = new FileWriter(file, false);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(dtd.generateJSON());
        out.close();
    }
}
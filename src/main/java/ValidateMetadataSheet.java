
import java.io.IOException;

import java.security.GeneralSecurityException;

import com.google.api.services.sheets.v4.Sheets;

import sheets.MetadataSheetParser;
import sheets.MetadataSheetValidator;
import sheets.SheetsService;

public class ValidateMetadataSheet {

    public static void main(String... args) throws IOException, GeneralSecurityException {
        final String spreadsheetId = "1R40Kl00sr1WskdiD7lXgEaG81jkkwH8xiSxEg0LSCFY";
        Sheets service = SheetsService.getService();
        MetadataSheetParser parser = new MetadataSheetParser(service, spreadsheetId);
        MetadataSheetValidator validator = new MetadataSheetValidator(parser);
        validator.runValidator();
    }
}
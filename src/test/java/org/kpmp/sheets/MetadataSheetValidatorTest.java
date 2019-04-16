package org.kpmp.sheets;

import com.google.api.services.sheets.v4.Sheets;
import org.junit.Before;
import org.junit.Test;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MetadataSheetValidatorTest {

    MetadataSheetParser parser;
    MetadataSheetValidator validator;
    private static final String sheetID = "1ttiMQyD4h-y7jTMBJhf_xu5kiWXGm2d-66D-KTCKTKk";

    @Before
    public void setUp() throws Exception {
        Sheets service = SheetsService.getService();
        parser = new MetadataSheetParser(service, sheetID);
        validator = new MetadataSheetValidator(parser);
    }

    @Test
    public void testRunValidator() throws Exception {
        validator.runValidator();
        TestLogger logger = TestLoggerFactory.getTestLogger(MetadataSheetValidator.class);
        List<LoggingEvent> events = logger.getAllLoggingEvents();
        assertEquals("Starting Metadata Sheet Validator", events.get(0).getMessage());
        assertEquals("Field \"Test Field No Dropdown Values\" is a drop-down but missing values.", events.get(1).getMessage());
        assertEquals("The fieldName \"fieldName1\" in \"Test Field Duplicate Fieldname\" already exists.", events.get(2).getMessage());
        assertEquals("Field \"Test Field No Fieldname\" has no fieldName.", events.get(3).getMessage());
        assertEquals("Field \"Test Field Vaildations No Props\" has a validation but no additional props.", events.get(4).getMessage());
        assertEquals("Field \"Test Field Bad Type\" has an invalid input type: Bad Type.", events.get(5).getMessage());
        assertEquals("Field \"Test Field No Display When Value\" Display When value (Missing value) missing for \"Test Field Linked\".", events.get(6).getMessage());
        assertEquals("Field \"Test Field No Display When\" is missing a Linked With value.", events.get(7).getMessage());
        assertEquals("The data types do not match the Package Type dropdown values.", events.get(8).getMessage());
        assertEquals("No column exists in sheet Proteomics for data type Dummy Data Type.", events.get(9).getMessage());
        assertEquals("No sheet exists for data type category Category with No Sheet.", events.get(10).getMessage());
    }

}

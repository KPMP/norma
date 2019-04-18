package org.kpmp.dtd;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SectionTest {

    private Section section;

    @Before
    public void setUp() throws Exception {
        section = new Section();
    }

    @After
    public void tearDown() throws Exception {
        section = null;
    }

    @Test
    public void testSetSectionHeader() {
        String header = "My Big Section";
        section.setSectionHeader(header);
        assertEquals(header, section.getSectionHeader());
    }

    @Test
    public void testSetFields() {
        Field field = new Field();
        ArrayList<Field> fields = new ArrayList<Field>();
        fields.add(field);
        section.setFields(fields);
        assertEquals(fields, section.getFields());
    }

}

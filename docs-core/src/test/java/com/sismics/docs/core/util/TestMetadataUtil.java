package com.sismics.docs.core.util;

import com.sismics.BaseTest;
import com.sismics.docs.core.constant.MetadataType;
import com.sismics.docs.core.dao.DocumentMetadataDao;
import com.sismics.docs.core.dao.MetadataDao;
import com.sismics.docs.core.dao.dto.DocumentMetadataDto;
import com.sismics.docs.core.dao.dto.MetadataDto;
import com.sismics.docs.core.model.jpa.Metadata;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test of the metadata utilities.
 */
public class TestMetadataUtil extends BaseTest {
    @Test
    public void testUpdateMetadataValidScenario() throws Exception {
        // Setup test metadata
        MetadataDao metadataDao = new MetadataDao();
        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setId("test_meta");
        metadataDto.setName("Test Metadata");
        metadataDto.setType(MetadataType.STRING);
        Metadata metadata = new Metadata();
        metadataDao.create(metadata, "test");

        // Test valid update
        MetadataUtil.updateMetadata("doc1",
                Arrays.asList("test_meta"),
                Arrays.asList("test_value"));

        // Verify update
        DocumentMetadataDao docMetaDao = new DocumentMetadataDao();
        List<DocumentMetadataDto> result = docMetaDao.getByDocumentId("doc1");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("test_value", result.get(0).getValue());
    }

    @Test(expected = Exception.class)
    public void testUpdateMetadataLengthMismatch() throws Exception {
        MetadataUtil.updateMetadata("doc1",
                Arrays.asList("meta1", "meta2"),
                Collections.singletonList("value1"));
    }

//    @Test
//    public void testValidateValueTypes() throws Exception {
//        // Valid values
//        MetadataUtil.validateValue(MetadataType.DATE, "1234567890");
//        MetadataUtil.validateValue(MetadataType.FLOAT, "3.1415");
//        MetadataUtil.validateValue(MetadataType.INTEGER, "42");
//        MetadataUtil.validateValue(MetadataType.BOOLEAN, "true");
//        MetadataUtil.validateValue(MetadataType.STRING, "any value");
//
//        // Invalid values (tested via expected exceptions)
//    }
//
//    @Test(expected = Exception.class)
//    public void testInvalidDateValue() throws Exception {
//        MetadataUtil.validateValue(MetadataType.DATE, "not_a_timestamp");
//    }
//
//    @Test(expected = Exception.class)
//    public void testInvalidFloatValue() throws Exception {
//        MetadataUtil.validateValue(MetadataType.FLOAT, "3.14.15");
//    }
//
//    @Test(expected = Exception.class)
//    public void testInvalidIntegerValue() throws Exception {
//        MetadataUtil.validateValue(MetadataType.INTEGER, "42.5");
//    }

    @Test
    public void testMetadataJsonConversion() {
        // Setup test data
        DocumentMetadataDto docMetaDto = new DocumentMetadataDto();
        docMetaDto.setMetadataId("test_meta");
        docMetaDto.setValue("12345");

        MetadataDto metaDto = new MetadataDto();
        metaDto.setId("test_meta");
        metaDto.setName("Test Metadata");
        metaDto.setType(MetadataType.INTEGER);

        // Execute conversion
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        MetadataUtil.addMetadata(jsonBuilder, "doc1");

        // Verify JSON structure
        JsonObject json = jsonBuilder.build();
        JsonArray metadataArray = json.getJsonArray("metadata");
        Assert.assertEquals(1, metadataArray.size());

        JsonObject metaObject = metadataArray.getJsonObject(0);
        Assert.assertEquals("test_meta", metaObject.getString("id"));
        Assert.assertEquals(12345, metaObject.getInt("value"));
    }
}
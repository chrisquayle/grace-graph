package com.chrisq.grace.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.chrisq.grace.graph.context.ContextGraph;
import com.chrisq.grace.graph.resource.ResourceState;
import com.chrisq.grace.graph.resource.ResourceVersion;
import com.chrisq.grace.graph.resource.TemporalResource;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class TemporalResourceTest {

    private ContextGraph contextGraph;
    private TemporalResource temporalResource;
    private ObjectMapper objectMapper;

    private Instant version0Date;
    private Instant version1Date;
    private Instant version2Date;
    private Instant version3Date;

    private static final String TITLE = "Planet";
    private static final String DESCRIPTION = "Describes a planet in a star system";
    private static final String PLANETARY_OBJECT = "Planetary Object";
    private static final String PLANETARY_DESCRIPTION = "Describes an object large enough to be classified as a planet";
    private static final String TITLE_FRANCE = "Planet";
    private static final String PLANETARY_DESCRIPTION_FRANCE = "Décrit un objet suffisamment grand pour être classé comme une planète";
    private static final String PLANET_TITLE = "Planet or Dwarf Planet";
    private static final String PLANET_DESCRIPTION = "Describes an object in orbit around a star";
    private static final String ADMIN_USER = "admin";
    private static final String REGULAR_USER = "ctombaugh";

    @Before
    public void setUp() {

        objectMapper = new ObjectMapper();

        version0Date = Instant.parse("1930-01-03T10:15:30.00Z");
        version1Date = Instant.parse("1950-01-03T10:15:30.00Z");
        version2Date = Instant.parse("2006-01-03T10:15:30.00Z");
        version3Date = Instant.parse("2012-01-03T10:15:30.00Z");

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        contextGraph = GraphSample.getSampleContextGraph();
        temporalResource = (TemporalResource) contextGraph.getNode(GraphSample.CTX_PLANET);
        // Changing planet definitions

        ResourceVersion version0 = new ResourceVersion(
                UUID.randomUUID(),
                temporalResource.getId(),
                TITLE,
                DESCRIPTION,
                Locale.US,
                REGULAR_USER,
                version0Date,
                ResourceState.PUBLISHED);
        temporalResource.addVersion(version0);

        ResourceVersion version1 = new ResourceVersion(
                UUID.randomUUID(),
                temporalResource.getId(),
                PLANETARY_OBJECT,
                PLANET_DESCRIPTION,
                Locale.US,
                ADMIN_USER,
                version1Date,
                ResourceState.PRIVATE);
        temporalResource.addVersion(version1);

        ResourceVersion version2 = new ResourceVersion(
                UUID.randomUUID(),
                temporalResource.getId(),
                TITLE_FRANCE,
                PLANETARY_DESCRIPTION_FRANCE,
                Locale.FRANCE,
                ADMIN_USER,
                version2Date,
                ResourceState.PRIVATE);
        temporalResource.addVersion(version2);

        ResourceVersion version3 = new ResourceVersion(
                UUID.randomUUID(),
                temporalResource.getId(),
                PLANET_TITLE,
                PLANET_DESCRIPTION,
                Locale.US,
                ADMIN_USER,
                version3Date,
                ResourceState.PRIVATE);
        temporalResource.addVersion(version3);
    }

    @Test
    public void shouldGetOriginalVersionTitleFragment(){
        temporalResource.setCreatedUtc(version1Date);
        assertTrue(temporalResource.getTitle().contains(TITLE));
        assertFalse(temporalResource.getTitle().equals(TITLE));
    }

    @Test
    public void shouldGetSecondVersionTitleFragment(){
        temporalResource.setCreatedUtc(version2Date);
        if (temporalResource.getVersions().contains(version2Date)) {
            assertTrue(temporalResource.getTitle().contains("Planetary Object"));
            assertFalse(temporalResource.getTitle().equals("Object"));
        }
    }

    @Test
    public void shouldGetSecondVersionInFrench(){
        ResourceVersion version;
        version = temporalResource.getVersions()
                .stream()
                .filter(resourceVersion -> resourceVersion.getCreatedUtc().equals(version2Date))
                .filter(resourceVersion -> resourceVersion.getLocale().equals(Locale.FRANCE))
                .collect(Collectors.toList())
                .get(0);
        assertTrue(version.getTitle().equalsIgnoreCase(TITLE_FRANCE));
        assertFalse(version.getTitle().equalsIgnoreCase(PLANETARY_DESCRIPTION));
    }
}
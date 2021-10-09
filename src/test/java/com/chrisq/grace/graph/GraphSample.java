package com.chrisq.grace.graph;

import com.chrisq.grace.graph.content.ContentGraph;
import com.chrisq.grace.graph.content.ContentLink;
import com.chrisq.grace.graph.content.ContentNode;
import com.chrisq.grace.graph.context.ContextGraph;
import com.chrisq.grace.graph.context.ContextLink;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.context.data.DataGraph;
import com.chrisq.grace.graph.context.data.DataLink;
import com.chrisq.grace.graph.context.data.DataResource;
import com.chrisq.grace.graph.context.form.field.*;
import com.chrisq.grace.graph.resource.TemporalResource;
import com.chrisq.grace.graph.util.BasicGraphProvider;

// A simple populated form to use in testing
public class GraphSample {
    private ContextGraph starSystemContext;
    private ContextGraph planetContext;
    private ContentGraph solarSystem;

    public static final String CTX_STAR_SYSTEM = "base:tcx:star-system";
    public static final String CTX_STAR = "base:tcxf:star";
    public static final String CTX_PLANETS = "base:tcxf:planets";
    public static final String CTX_PLANET = "base:tcx:planet";
    public static final String CTX_PLANET_ID = "base:tcxf:planet-id";
    public static final String CTX_SATELLITE = "base:tcxf:satellite";
    public static final String CTX_ATMOSPHERE_ELEMENTS = "base:tcxf:atmosphere-elements";
    public static final String CTX_ATMOSPHERE_ELEMENT = "base:tcxf:atmosphere-element";
    public static final String CTX_ATMOSPHERE_ELEMENT_O = CTX_ATMOSPHERE_ELEMENT + ":oxygen";
    public static final String CTX_ATMOSPHERE_ELEMENT_N = CTX_ATMOSPHERE_ELEMENT +  ":nitrogen";
    public static final String CTX_ATMOSPHERE_ELEMENT_H = CTX_ATMOSPHERE_ELEMENT +  ":hydrogen";
    public static final String CTX_ATMOSPHERE_ELEMENT_X = CTX_ATMOSPHERE_ELEMENT +  ":xenon";
    public static final String DATAGRAPH_ATMOSPHERE_ELEMENTS = "base:td:atmosphere-elements";
    public static final String DATA_ATMOSPHERE_ELEMENT = "base:tdrs:atmosphere-element";
    public static final String DATA_ATMOSPHERE_ELEMENT_O = DATA_ATMOSPHERE_ELEMENT + ":oxygen";
    public static final String DATA_ATMOSPHERE_ELEMENT_N = DATA_ATMOSPHERE_ELEMENT +  ":nitrogen";
    public static final String DATA_ATMOSPHERE_ELEMENT_H = DATA_ATMOSPHERE_ELEMENT +  ":hydrogen";
    public static final String DATA_ATMOSPHERE_ELEMENT_X = DATA_ATMOSPHERE_ELEMENT +  ":xenon";

    public GraphSample() throws TouchContextException {
        // Create context (schema)
        starSystemContext = new ContextGraph(new ContextResourceId(CTX_STAR_SYSTEM));
        Field cstar = new ObjectField(new ContextResourceId(CTX_STAR));
        //ContextNode cplanets = new ContextArray(new ContextResourceId(CTX_PLANET));

        planetContext = new ContextGraph(new ContextResourceId(CTX_PLANET));
        TextField cplanetId = new TextField(new ContextResourceId(CTX_PLANET_ID));
        Field csattelite = new TextField(new ContextResourceId(CTX_SATELLITE));

        // Static data graph to contain lookup lists
        DataGraph atmosphereElementsGraph = new DataGraph(new TemporalResource(
                DATAGRAPH_ATMOSPHERE_ELEMENTS,
                "Atmospheric Elements",
                "Elements that make up atmospheres"));
         atmosphereElementsGraph.addLink(new DataLink(
                new DataResource(DATAGRAPH_ATMOSPHERE_ELEMENTS),
                new DataResource(new TemporalResource(new TemporalResource(DATA_ATMOSPHERE_ELEMENT_H, "H-Hydrogen", "Hydrogen")))));
        atmosphereElementsGraph.addLink(new DataLink(
                new DataResource(DATAGRAPH_ATMOSPHERE_ELEMENTS),
                new DataResource(new TemporalResource(new TemporalResource(DATA_ATMOSPHERE_ELEMENT_O, "O-Oxygen", "Oxygen")))));
        atmosphereElementsGraph.addLink(new DataLink(
                new DataResource(DATAGRAPH_ATMOSPHERE_ELEMENTS),
                new DataResource(new TemporalResource(new TemporalResource(DATA_ATMOSPHERE_ELEMENT_N, "H-Nitrogen", "Nitrogen")))));
        atmosphereElementsGraph.addLink(new DataLink(
                new DataResource(DATAGRAPH_ATMOSPHERE_ELEMENTS),
                new DataResource(new TemporalResource(new TemporalResource(DATA_ATMOSPHERE_ELEMENT_X, "X-Xenon", "Xenon")))));

        TextField catmosphereElements = new TextField(new ContextResourceId(CTX_ATMOSPHERE_ELEMENTS));
        catmosphereElements.setMultiValueDataList(true);
        catmosphereElements.setDataListId(new ContextResourceId(DATAGRAPH_ATMOSPHERE_ELEMENTS));
        //  Fields that are listable expect to be able to pull in data from static lists stored in the context graph if setUserStaticList = true
        catmosphereElements.setLocalDataList(true);
        // Add the datalist to the static data resources in the context graph. Fields look for ref data here if static lookups are enabled
        planetContext.getDataGraphs().put(new ContextResourceId(DATAGRAPH_ATMOSPHERE_ELEMENTS), atmosphereElementsGraph);
        // Set the list as the source of static options
        catmosphereElements.setDataList(atmosphereElementsGraph.getDataList(DATAGRAPH_ATMOSPHERE_ELEMENTS));
        ContextArray cplanet = new ContextArray(new ContextResourceId(CTX_PLANET), new ContextReference(planetContext.getContextResourceId()));
        cplanet.setMinItems(0);
        cplanet.setMaxItems(null);

        starSystemContext.setContentKeyFieldId(cstar.getId());
        starSystemContext.addVersion("Star System Context", "Describes a star system and its planets with their satellites");
        starSystemContext.addLink(new ContextLink(cstar, cplanet));

        planetContext.setContentKeyFieldId(cplanetId.getId());
        planetContext.addVersion("Planet Context", "Describes a planetary body");
        planetContext.addLink(new ContextLink(cplanet, cplanetId));
        planetContext.addLink(new ContextLink(cplanet, catmosphereElements));
        planetContext.addLink(new ContextLink(cplanet, csattelite));

        starSystemContext.getRefGraphs().put(planetContext.getId(), planetContext);

        ContentNode sun = new ContentNode((Field)cstar, "sun");
        ContentNode mercury = new ContentNode(cplanet, "mercury");
        ContentNode venus = new ContentNode(cplanet, "venus");
        ContentNode earth = new ContentNode(cplanet, "earth");
        ContentNode earthAtmosphere = new ContentNode(catmosphereElements, "earth-atmosphere");
        ContentNode oxygen = new ContentNode(new TextField(new ContextResourceId(CTX_ATMOSPHERE_ELEMENT_O)), DATA_ATMOSPHERE_ELEMENT_O);
        ContentNode nitrogen = new ContentNode(new TextField(new ContextResourceId(CTX_ATMOSPHERE_ELEMENT_N)), DATA_ATMOSPHERE_ELEMENT_N);
        ContentNode hydrogen = new ContentNode(new TextField(new ContextResourceId(CTX_ATMOSPHERE_ELEMENT_H)), DATA_ATMOSPHERE_ELEMENT_H);
        ContentNode xenon = new ContentNode(new TextField(new ContextResourceId(CTX_ATMOSPHERE_ELEMENT_X)), DATA_ATMOSPHERE_ELEMENT_X);

        ContentNode moon = new ContentNode(csattelite, "moon");
        ContentNode iss = new ContentNode(csattelite, "iss");

        // Spoof a storage directory
        BasicGraphProvider provider = new BasicGraphProvider(starSystemContext);

        // Create the content graph
        solarSystem = new ContentGraph(provider, null, starSystemContext.getContextResourceId(), "base:tcn:solar-system");

        ContentLink sunMercury = new ContentLink(solarSystem.getId(), sun, mercury, 0);
        ContentLink sunVenus = new ContentLink(solarSystem.getId(), sun, venus, 1);
        ContentLink sunEarth = new ContentLink(solarSystem.getId(), sun, earth, 2);
        ContentLink earthMoon = new ContentLink(solarSystem.getId(), earth, moon, 3);
        ContentLink earthIss = new ContentLink(solarSystem.getId(), earth, iss, 4);
        ContentLink earthEarthAtmosphere = new ContentLink(solarSystem.getId(), earth, earthAtmosphere, 5);
        ContentLink earthAtmosphereElementsO = new ContentLink(solarSystem.getId(), earthAtmosphere, oxygen, 6);
        ContentLink earthAtmosphereElementsN = new ContentLink(solarSystem.getId(), earthAtmosphere, nitrogen, 7);
        ContentLink earthAtmosphereElementsH = new ContentLink(solarSystem.getId(), earthAtmosphere, hydrogen, 8);

        solarSystem.addLink(sunMercury);
        solarSystem.addLink(sunEarth);
        solarSystem.addLink(sunVenus);
        solarSystem.addLink(earthMoon);
        solarSystem.addLink(earthIss);
        solarSystem.addLink(earthEarthAtmosphere);
        solarSystem.addLink(earthAtmosphereElementsO);
        solarSystem.addLink(earthAtmosphereElementsN);
        solarSystem.addLink(earthAtmosphereElementsH);
    }

    public static ContextGraph getSampleContextGraph() {
        return new GraphSample().starSystemContext;
    }

    public static ContentGraph getSampleContentGraph() {
        return new GraphSample().solarSystem;
    }

}

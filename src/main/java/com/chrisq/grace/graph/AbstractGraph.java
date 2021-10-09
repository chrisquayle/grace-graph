package com.chrisq.grace.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.resource.ResourceType;
import com.chrisq.grace.graph.resource.TemporalResource;
import com.chrisq.grace.graph.tree.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO: file header
 */
public abstract class AbstractGraph extends TemporalResource implements Graph, Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraph.class);

    // Stores a timestamp for each time the graph was updated. These timestamps will correspond to the insert time on the links that were updated at the time
    private final Set<GraphVersion> graphVersions = new LinkedHashSet<>();
    // Stores all versions of all links (properties) for this graph.
    private final LinkedHashMap<String, Link> unversionedLinks = new LinkedHashMap<>();
    // Stores pointers to any graphs that are referenced by this one
    private final Map<String, Graph> refGraphs = new LinkedHashMap<>();
    private String parentGraphId = null;
    private GraphSharingType graphSharingType = GraphSharingType.BOUND_GRAPH;

    protected AbstractGraph() {
        super();
    }

    public AbstractGraph(String id) {
        super(id);
    }

    public AbstractGraph(TemporalResource fromResource) {
        super(fromResource);
    }



    @Override
    // Handle version mapping. This maps only eligible versions of properties that apply to the graph, depending on when fields were updated
    public LinkedHashMap<String, Link> getLinks() {
        // TODO: Improve efficiency: Store the versioned links list and only re-run the logic if the link versions have changed
        if(this.graphVersions.isEmpty()) return this.unversionedLinks;
        // Ensure that the graph version timestamps are filtered for this graph ID and also in descending order
        LinkedList<GraphVersion> sortedGraphVersions =
                graphVersions.stream()
                        .filter(v -> v.getParentGraphId() != null && v.getParentGraphId().equalsIgnoreCase(this.getParentGraphId()))
                        .sorted(Comparator.comparing(GraphVersion::getVersionUtc).reversed())
                        .collect(Collectors.toCollection(LinkedList::new));
        // Same for the link versions for this the graph
        List<Link> linkVersionsDescending =
                unversionedLinks.values().stream()
                        .sorted(Comparator.comparing(Link::getCreatedUtc).reversed())
                        .collect(Collectors.toCollection(LinkedList::new));
        LinkedList<Link> versionedLinks = new LinkedList<>();
        // Find the latest version before or at the asAtUtc time marker
        Instant latestVersion = this.getAsAtUtc();
        for(GraphVersion version : sortedGraphVersions){
            if(version.getVersionUtc().equals(this.getAsAtUtc()) || version.getVersionUtc().isBefore(this.getAsAtUtc())){
                latestVersion = version.getVersionUtc();
                break;
            }
        }
        // Process each link and figure out if it's the latest version of the link for the current time marker (asAtUtc)
        for (Link l : linkVersionsDescending) {
            if (l.getCreatedUtc().equals(latestVersion) || l.getCreatedUtc().isBefore(latestVersion)) {
                for (GraphVersion v : sortedGraphVersions) {
                    if (l.getCreatedUtc().equals(v.getVersionUtc())) {
                        // The property was updated as part of this version, so use it in the graph
                        if(!versionedLinks.contains(l)) versionedLinks.add(l);
                        // Stop processing versions for this link - we only want the latest version of each property (before asAtUtc) to be returned
                        break;
                    }
                }
                // If no version was matched then add the link as it is - there are no later verions so it must be the original version
                if(!versionedLinks.contains(l)) versionedLinks.add(l);
            }
        }
        // Ensure that the links are sorted by the sort index ID and create the key map
        Collections.sort(versionedLinks, Comparator.comparing(Link::getSortIndex));
        LinkedHashMap versionedLinksMap = new LinkedHashMap();
        versionedLinks.forEach(l -> {
            versionedLinksMap.put(l.getId(), l);
        });
        return versionedLinksMap;
    }

    public void addGraphVersion(String parentGraphId, Instant version){
        this.graphVersions.add(new GraphVersion(this.getId(), parentGraphId, version));
    }

    public Set<GraphVersion> getGraphVersions() {
        return graphVersions;
    }

    public GraphVersion getLatestVersion(){
        Optional version = graphVersions.stream()
                .filter(v -> v.getParentGraphId() != null && v.getParentGraphId().equalsIgnoreCase(this.getParentGraphId()))
                .sorted(Comparator.comparing(GraphVersion::getVersionUtc).reversed())
                .findFirst();
        return version.isPresent() ? (GraphVersion)version.get() : null;
    }

    @Override
    public LinkedHashMap<String, Link> getLinks(Node node) {
        LinkedHashMap<String, Link> results = new LinkedHashMap<>();
        for (Link link : this.getLinks().values()) {
            if (link.getSource().getId().equalsIgnoreCase(node.getId())) {
                results.put(link.getId(), link);
            }
        }
        return results;
    }

    public LinkedHashMap<String, Link> getLinks(ResourceType type) {
        LinkedHashMap<String, Link> results = new LinkedHashMap<>();
        for (Link link : this.getLinks().values()) {
            if (link.getTarget().getResourceType().equals(type)) {
                results.put(link.getId(), link);
            }
        }
        return results;
    }

    public Link getLink(String key) {
        return this.getLinks().get(key);
    }

    public Link getLinkByTargetId(String targetId) {
        return this.getLinks().values().stream().filter(n -> n.getTarget().getId().equalsIgnoreCase(targetId)).findFirst().orElse(null);
    }

    public Link getLinkBySourceId(String sourceId) {
        return this.getLinks().values().stream().filter(n -> n.getSource().getId().equalsIgnoreCase(sourceId)).findFirst().orElse(null);
    }

    public Node getNode(String nodeId) {
        return this.getNodes().get(nodeId);
    }

    public Map<String, Link> getUnversionedLinks() {
        return unversionedLinks;
    }

    @Override
    public void setAsAtUtc(Instant asAtUtc) {
        super.setAsAtUtc(asAtUtc);
        for(Graph refGraph : this.getRefGraphs().values()){
            refGraph.setAsAtUtc(asAtUtc);
        }
    }

    @JsonIgnore
    public Map<String, Node> getSourceNodes() {
        Map<String, Node> nodes = new HashMap<>();
        for (Link link : this.getLinks().values()) {
            nodes.put(link.getSourceId(), link.getSource());
//            for (Graph refGraph : this.refGraphs.values()) {
//                nodes.putAll(((AbstractGraph) refGraph).getSourceNodes());
//            }
        }
        return nodes;
    }

    @JsonIgnore
    public Map<String, Node> getTargetNodes() {
        Map<String, Node> nodes = new HashMap<>();
        for (Link link : this.getLinks().values()) {
            nodes.put(link.getTargetId(), link.getTarget());
//            for (Graph refGraph : this.refGraphs.values()) {
//                nodes.putAll(((AbstractGraph) refGraph).getTargetNodes());
//            }
        }
        return nodes;
    }

    @JsonIgnore
    public Map<String, Node> getNodes() {
        Map<String, Node> nodes = new HashMap<>();
        nodes.putAll(this.getSourceNodes());
        nodes.putAll(this.getTargetNodes());
        return nodes;
    }


    public String getParentGraphId() {
        return parentGraphId;
    }

    public void setParentGraphId(String parentGraphId) {
        this.parentGraphId = parentGraphId;
    }

    @Override
    public LinkedHashMap<String, Link> getRootLinks() {
        // Find properties where there is no incoming node
        // A -> B
        // B -> C
        // A-> C
        // B and C are both in the target role so cant be root
        // A is only in the source role so must be a root
        List<Link> rootLinks = new LinkedList<>();
        Map<String, Node> targetNodes = this.getTargetNodes();
        for (Link link : this.getLinks().values()) {
            if (link.getSource() == null || !targetNodes.keySet().contains(link.getSourceId())) {
                rootLinks.add(link);
            }
        }
        rootLinks.sort(Comparator.comparing(Link::getSortIndex));
        LinkedHashMap<String, Link> sortedMap = new LinkedHashMap<>();
        rootLinks.forEach(l -> sortedMap.put(l.getId(), l));
        return sortedMap;
    }

    @Override
    public void addLink(Link link) throws com.chrisq.grace.graph.resource.ResourceException {
        link.setState(this.getState());
        link.getTarget().setState(link.getState());
        link.getSource().setState(link.getState());
        this.unversionedLinks.put(link.getId(), link);
        LOG.trace("Graph {} link version added: {}", this.getId(), link.getId());
    }

    @Override
    // Default output as D3 base JSON
    public String toString() {
        return String.join("", super.toString(), ", ", this.toD3GraphJson());
    }

    public String toJsonString() {
        String stringBuilder = "{" +
                super.toString() +
                "," +
                "\"nodes\":[" +
                this.getNodes().values().stream().map(Node::toString).collect(Collectors.joining(",")) +
                "],\"links\":[" +
                this.getLinks().values().stream().map(Link::toString).collect(Collectors.joining(",")) +
                "]" +
                "}";
        return stringBuilder;
    }

    @Override
    public Tree toTree() throws TouchContextException {
        return new Tree(this);
    }

    @Override
    public Map<String, Graph> getRefGraphs() {
        return refGraphs;
    }

    public Graph getRefGraphById(String id) {
        for (Graph graph : this.refGraphs.values()) {
            if (graph.getId().equalsIgnoreCase(id)) {
                return graph;
            }
        }
        return null;
    }

    public GraphSharingType getGraphSharingType() {
        return graphSharingType;
    }

    public void setGraphSharingType(GraphSharingType graphSharingType) {
        this.graphSharingType = graphSharingType;
    }

    // Return JSON data which supports a force-directed D3 network base
    public String toD3GraphJson() {
        String sb = "{\"nodes\":[" +
                this.getNodes().values().stream().map(node -> node.toGraphJson()).collect(Collectors.joining(",")) +
                "],\"links\":[" +
                this.getLinks().values().stream().map(link -> link.toD3GraphJson()).collect(Collectors.joining(",")) +
                "]}";
        return sb;
    }
}

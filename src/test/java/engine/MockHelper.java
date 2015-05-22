package engine;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.parser.visitor.RamlDocumentBuilder;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;

public class MockHelper {

  public MockHelper() {
    super();
  }

  protected void addMapEntryWith(Map<ActionId, Action> map, ActionType type, String uri) {
    ActionId id = new ActionId(type, uri);
    map.put(id, mockActionWith(type, uri));
  }


  protected Action mockActionWith(ActionType type, String uri) {
    Action actionMock = mock(Action.class, RETURNS_DEEP_STUBS);
    when(actionMock.getType()).thenReturn(type);
    when(actionMock.getResource().getUri()).thenReturn(uri);
    when(actionMock.toString()).thenCallRealMethod();
    return actionMock;
  }

  protected void addMapEntryWithTraits(Map<ActionId, Action> map, ActionType type, String uri, List<String> traits) {
    ActionId id = new ActionId(type, uri);
    map.put(id, mockActionWithTraits(type, uri, traits));
  }

  protected Action mockActionWithTraits(ActionType type, String uri, List<String> traits) {
    Action actionMock = mock(Action.class, RETURNS_DEEP_STUBS);
    when(actionMock.getType()).thenReturn(type);
    when(actionMock.getResource().getUri()).thenReturn(uri);
    when(actionMock.getIs()).thenReturn(traits);
    when(actionMock.toString()).thenCallRealMethod();
    return actionMock;
  }

  protected Set<ActionType> getSetOfActionTypes(List<ActionDiff> diff) {
    return diff.stream().map(i -> {
      return i.getAction().getType();
    }).collect(Collectors.toSet());
  }

  protected long getDistinctDiffTypeCount(List<ActionDiff> diff) {
    return diff.stream().map(i -> {
      return i.getDiffType();
    }).distinct().count();
  }

  protected DiffType getDistinctDiffType(List<ActionDiff> diff) {
    return diff.stream().map(i -> {
      return i.getDiffType();
    }).distinct().findAny().get();
  }

  protected Stream<ActionDiff> filterByDiffType(List<ActionDiff> allDifferences, DiffType differenceType) {
    return allDifferences.stream().filter(i -> {
      boolean result = false;
      if (i.getDiffType().equals(differenceType)) {
        result = true;
      }
      return result;
    });
  }

  public Set<DiffType> getSetOfDiffTypes(List<ActionDiff> diff) {
    return diff.stream().map(i -> {
      return i.getDiffType();
    }).collect(Collectors.toSet());
  }

  public Collection<Resource> retrieveResources(String filePath) throws FileNotFoundException {
    Collection<Resource> resources = new ArrayList<Resource>();
    File file = new File(filePath);

    RamlDocumentBuilder documentBuilder = new RamlDocumentBuilder();
    FileInputStream fis = new FileInputStream(file);
    Raml ramlFile = documentBuilder.build(fis, file.getAbsolutePath());
    Map<String, Resource> resourcesMap = ramlFile.getResources();
    resources = flattenResources(resourcesMap);
    return resources;
  }

  private Collection<Resource> flattenResources(Map<String, Resource> resources) {
    Collection<Resource> nested = new ArrayList<Resource>();
    Collection<Resource> resourceValues = resources.values();
    for (Resource r : resourceValues) {
      nested.add(r);
      if (r.getResources().values().size() > 0) {
        nested.addAll(flattenResources(r.getResources()));
      }
    }
    return nested;
  }

  protected Map<ActionId, Action> getRamlActionsFor(Collection<Resource> resources) {
    //@formatter:off
    return resources.stream()
        .flatMap(resource -> {
            return resource.getActions().values().stream();
        })
        .collect(Collectors.toMap(
            a -> new ActionId(a), 
            a -> a
        ));
    //@formatter:on
  }

  // Methods for Mocking Actions With Response
  protected void addMapEntryWithResponse(Map<ActionId, Action> map, ActionType type, String uri,
      Map<String, Response> responses) {
    ActionId id = new ActionId(type, uri);
    map.put(id, mockActionWithResponse(type, uri, responses));
  }

  protected Action mockActionWithResponse(ActionType type, String uri, Map<String, Response> responses) {
    Action actionMock = mock(Action.class, RETURNS_DEEP_STUBS);
    when(actionMock.getType()).thenReturn(type);
    when(actionMock.getResponses()).thenReturn(responses);
    when(actionMock.toString()).thenCallRealMethod();
    return actionMock;
  }

  protected Map<String, Response> mockResponse() {
    Map<String, MimeType> body = new HashMap<String, MimeType>();
    MimeType type = new MimeType();
    type.setSchema("application/json");
    body.put("application_json", type);
    Response response = new Response();
    response.setBody(body);

    Map<String, Response> responses = new HashMap<String, Response>();
    responses.put("200", response);

    return responses;
  }

  protected Map<String, Response> mockResponseWithAddedMime() {
    Map<String, MimeType> body = new HashMap<String, MimeType>();
    MimeType type = new MimeType();
    type.setSchema("application/Json");
    body.put("application_json", type);

    type = new MimeType();
    type.setSchema("application/xml");
    body.put("application_xml", type);
    Response response = new Response();
    response.setBody(body);

    Map<String, Response> responses = new HashMap<String, Response>();
    responses.put("200", response);

    return responses;
  }

  protected Map<String, Response> mockResponseWithDeletedMime() {
    Response response = new Response();
    response.setBody(null);

    Map<String, Response> responses = new HashMap<String, Response>();
    responses.put("200", response);

    return responses;
  }

  protected Map<String, Response> mockResponseWithModifiedMimeSchema() {

    Map<String, MimeType> body = new HashMap<String, MimeType>();
    MimeType type = new MimeType();
    type.setSchema("application/json1");
    body.put("application_json", type);

    Response response = new Response();
    response.setBody(body);

    Map<String, Response> responses = new HashMap<String, Response>();
    responses.put("200", response);

    return responses;
  }

}

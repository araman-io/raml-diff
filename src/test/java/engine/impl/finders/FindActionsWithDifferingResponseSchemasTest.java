package engine.impl.finders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Response;

import diff.ActionDiff;
import diff.ActionId;
import engine.MockHelper;

public class FindActionsWithDifferingResponseSchemasTest extends MockHelper {


  @Test
  public void instancesOfThisClassShouldBeEqual() {
    assertEquals(new FindActionsWithDifferingResponseSchemas(), new FindActionsWithDifferingResponseSchemas());
  }
  
  @Test
  public void instancesOfDifferentFindersShouldNotBeEqual() {
    assertFalse(new FindActionsWithDifferingResponseSchemas().equals(new FindActionsWithUpdatedTraits()));
  }

  @Test
  public void shouldFindOneActionCommon() {
    ActionId a = new ActionId(ActionType.GET, "/foobar");
    ActionId oa = new ActionId(ActionType.GET, "/foobar");

    Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
    newActionMap.put(a, mock(Action.class));

    Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
    oldActionMap.put(oa, mock(Action.class));

    FindActionsWithDifferingResponseSchemas finder = new FindActionsWithDifferingResponseSchemas();
    Collection<ActionId> commonActionIds = finder.getCommonActions(newActionMap, oldActionMap);
    assertEquals(1, commonActionIds.size());
  }

  @Test
  public void shouldFindNoActionCommon() {
    ActionId a = new ActionId(ActionType.GET, "/foobar");
    ActionId b = new ActionId(ActionType.GET, "/foobar2");
    ActionId oa = new ActionId(ActionType.GET, "/foobaraa");

    Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
    newActionMap.put(a, mock(Action.class));
    newActionMap.put(b, mock(Action.class));

    Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
    oldActionMap.put(oa, mock(Action.class));

    FindActionsWithDifferingResponseSchemas finder = new FindActionsWithDifferingResponseSchemas();
    Collection<ActionId> commonActionIds = finder.getCommonActions(newActionMap, oldActionMap);
    assertEquals(0, commonActionIds.size());
  }

  @Test
  public void shouldReturnOneSchemaForAction() {
    MimeType mimeType = mock(MimeType.class, RETURNS_DEEP_STUBS);
    Map<String, MimeType> mimeTypeMap = new HashMap<String, MimeType>();
    when(mimeType.getSchema()).thenReturn("schema 1");
    mimeTypeMap.put("application/json", mimeType);


    Response response = mock(Response.class, RETURNS_DEEP_STUBS);
    Map<String, Response> responseMap = new HashMap<String, Response>();
    responseMap.put("200", response);
    when(response.getBody()).thenReturn(mimeTypeMap);

    FindActionsWithDifferingResponseSchemas finder = new FindActionsWithDifferingResponseSchemas();
    Set<String> collatedSchema = finder.getCollatedSchemaForResponseWithStatusCode("200", responseMap);

    assertEquals(1, collatedSchema.size());
    assertTrue(collatedSchema.contains("schema 1"));
  }

  @Test
  public void shouldReturnTwoSchemasForAction() {
    MimeType mimeTypeJson = mock(MimeType.class, RETURNS_DEEP_STUBS);
    when(mimeTypeJson.getSchema()).thenReturn("json schema 1");

    MimeType mimeTypeProtoBuf = mock(MimeType.class, RETURNS_DEEP_STUBS);
    when(mimeTypeProtoBuf.getSchema()).thenReturn("proto buf definition");

    Map<String, MimeType> mimeTypeMap = new HashMap<String, MimeType>();
    mimeTypeMap.put("application/json", mimeTypeJson);
    mimeTypeMap.put("protobuf", mimeTypeProtoBuf);

    Response response = mock(Response.class, RETURNS_DEEP_STUBS);
    Map<String, Response> responseMap = new HashMap<String, Response>();
    responseMap.put("200", response);
    when(response.getBody()).thenReturn(mimeTypeMap);

    FindActionsWithDifferingResponseSchemas finder = new FindActionsWithDifferingResponseSchemas();
    Set<String> collatedSchema = finder.getCollatedSchemaForResponseWithStatusCode("200", responseMap);

    assertEquals(2, collatedSchema.size());
    assertTrue(collatedSchema.contains("json schema 1"));
  }

  @Test
  public void shouldReturnOneSchemaDifference() {

    Map<ActionId, Action> newResponse = this.returnActionMapWithSchema("json 200 schema new response");
    Map<ActionId, Action> oldResponse = this.returnActionMapWithSchema("json 200 schema old response");

    FindActionsWithDifferingResponseSchemas finder = new FindActionsWithDifferingResponseSchemas();
    List<ActionDiff> diff = finder.diff(newResponse, oldResponse);

    assertEquals(1, diff.size());
  }
  
  @Test
  public void shouldReturnNoSchemaDifference() {

    Map<ActionId, Action> newResponse = this.returnActionMapWithSchema("json 200 schema response");
    Map<ActionId, Action> oldResponse = this.returnActionMapWithSchema("json 200 schema response");

    FindActionsWithDifferingResponseSchemas finder = new FindActionsWithDifferingResponseSchemas();
    List<ActionDiff> diff = finder.diff(newResponse, oldResponse);
    assertEquals(0, diff.size());
  }


  public Map<ActionId, Action> returnActionMapWithSchema(String inputSchema) {
    Response response200 = mock(Response.class, RETURNS_DEEP_STUBS);
    MimeType mimeType200 = mock(MimeType.class);
    when(mimeType200.getSchema()).thenReturn(inputSchema);
    Map<String, MimeType> mimeTypeMap = new HashMap<String, MimeType>();
    mimeTypeMap.put("json", mimeType200);
    when(response200.getBody()).thenReturn(mimeTypeMap);
    Action action = mock(Action.class, RETURNS_DEEP_STUBS);
    Map<String, Response> responseMap = new HashMap<String, Response>();
    responseMap.put("200", response200);
    when(action.getResponses()).thenReturn(responseMap);
    ActionId id = new ActionId(ActionType.GET, "/foo");

    Map<ActionId, Action> actionMap = new HashMap<ActionId, Action>();
    actionMap.put(id, action);

    return actionMap;
  }
}

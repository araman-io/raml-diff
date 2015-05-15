package engine.impl.finders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Response;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import engine.MockHelper;

public class FindActionsWithUpdatedResponseStatusCodeTest extends MockHelper {

  Map<ActionId, Action> newActions = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActions = new HashMap<ActionId, Action>();
  FindActionsWithUpdatedResponseStatusCode classToTest = new FindActionsWithUpdatedResponseStatusCode();

  @Before
  public void init() {
    addMapEntryWithResponse(newActions, ActionType.GET, "/test", mockResponse());
    addMapEntryWith(oldActions, ActionType.GET, "/test");
  }


  @Test
  public void shouldReturnOneAdditionOfResponseWhenAResponseIsAddedToNewRaml() {
    assertEquals(1, classToTest.diff(newActions, oldActions).size());
  }

  @Test
  public void shouldReturnOneDeletionOfResponseWhenAResponseIsDeletedFromNewRaml() {
    assertEquals(1, classToTest.diff(oldActions, newActions).size());
  }

  @Test
  public void shouldReturnNoChanegeInResponseWhenAResponseIsNeitherAddedNorRemoved() {
    assertEquals(0, classToTest.diff(oldActions, oldActions).size());
    assertEquals(0, classToTest.diff(newActions, newActions).size());
  }

  @Test
  public void twoInstancesOfFindActionsWithUpdatedResponseStatusCodeShouldBeEqual() {
    assertEquals(new FindActionsWithUpdatedResponseStatusCode(), new FindActionsWithUpdatedResponseStatusCode());
  }

  @Test
  public void shouldReportEachStatusCodeAdditionDeletionAsSeperateDiffEvents() {
    Map<ActionId, Action> newActions = new HashMap<ActionId, Action>();
    Map<ActionId, Action> oldActions = new HashMap<ActionId, Action>();

    Map<String, Response> responses = new HashMap<String, Response>();
    responses.put("200", mock(Response.class, RETURNS_DEEP_STUBS));
    responses.put("301", mock(Response.class, RETURNS_DEEP_STUBS));
    Action mockedActionWith2StatusCodes = super.mockActionWithResponse(ActionType.GET, "/foobar", responses);

    newActions.put(new ActionId(ActionType.GET, "/foobar"), mockedActionWith2StatusCodes);
    addMapEntryWith(oldActions, ActionType.GET, "/foobar");

    List<ActionDiff> differences = classToTest.diff(newActions, oldActions);
    assertEquals(2, differences.size());
    assertEquals(DiffType.NEW, super.getDistinctDiffType(differences));
  }

  @Test
  public void shouldReport2NewStatusCodeAnd1DeletedStatusCodeAsDiffEvents() {
    Map<ActionId, Action> newActions = new HashMap<ActionId, Action>();
    Map<ActionId, Action> oldActions = new HashMap<ActionId, Action>();

    Map<String, Response> newResponses = new HashMap<String, Response>();
    newResponses.put("200", mock(Response.class, RETURNS_DEEP_STUBS));
    newResponses.put("301", mock(Response.class, RETURNS_DEEP_STUBS));
    Action newActionWith2StatusCodes = super.mockActionWithResponse(ActionType.GET, "/foobar", newResponses);
    newActions.put(new ActionId(ActionType.GET, "/foobar"), newActionWith2StatusCodes);

    Map<String, Response> oldResponses = new HashMap<String, Response>();
    oldResponses.put("303", mock(Response.class, RETURNS_DEEP_STUBS));
    Action oldActionWith1StatusCodes = super.mockActionWithResponse(ActionType.GET, "/foobar", oldResponses);
    oldActions.put(new ActionId(ActionType.GET, "/foobar"), oldActionWith1StatusCodes);

    List<ActionDiff> differences = classToTest.diff(newActions, oldActions);
    assertEquals(3, differences.size());
    assertEquals(2, super.getDistinctDiffTypeCount(differences));
    assertEquals(new HashSet<DiffType>(Arrays.asList(DiffType.NEW, DiffType.DELETED)),
        super.getSetOfDiffTypes(differences));
  }
}

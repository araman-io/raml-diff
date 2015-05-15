package engine.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import engine.Finder;
import engine.MockHelper;
import engine.RamlDiffEngine;
import engine.impl.finders.FindActionsWithUpdatedQueryParameters;
import engine.impl.finders.FindActionsWithUpdatedResponseSchema;
import engine.impl.finders.FindActionsWithUpdatedResponseStatusCode;
import engine.impl.finders.FindActionsWithUpdatedTraits;
import engine.impl.finders.FindNewActions;
import engine.impl.finders.FindRemovedActions;

public class RamlDiffEngineImplTest extends MockHelper {

  Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
  List<Finder> EXPECTED_FINDER_ORDER = Arrays.asList(new FindNewActions(), new FindRemovedActions(),
      new FindActionsWithUpdatedQueryParameters(), new FindActionsWithUpdatedTraits(),
      new FindActionsWithUpdatedResponseStatusCode(), new FindActionsWithUpdatedResponseSchema());
  private RamlDiffEngine ramlDiffEngine = new RamlDiffEngineImpl();


  @Before
  public void initTest() throws Exception {
    List<String> traits = new ArrayList<String>();
    traits.add("historical");

    addMapEntryWith(newActionMap, ActionType.GET, "/foo");
    addMapEntryWith(newActionMap, ActionType.DELETE, "/foo");

    addMapEntryWith(oldActionMap, ActionType.GET, "/foo");
    addMapEntryWith(oldActionMap, ActionType.DELETE, "/foo/bar");
    addMapEntryWith(oldActionMap, ActionType.PATCH, "/foo/bar");
  }

  @Test
  public void shouldReportOverallThreeActionDifferences() {
    List<ActionDiff> allDifferences = ramlDiffEngine.findDifferences(newActionMap, oldActionMap);
    assertEquals(3, allDifferences.size());
  }

  @Test
  public void shouldReportOneNewAction() {
    List<ActionDiff> allDifferences = ramlDiffEngine.findDifferences(newActionMap, oldActionMap);
    assertEquals(1, filterByDiffType(allDifferences, DiffType.NEW).count());
  }

  @Test
  public void shouldReport2DeletedAction() {
    List<ActionDiff> allDifferences = ramlDiffEngine.findDifferences(newActionMap, oldActionMap);
    assertEquals(2, filterByDiffType(allDifferences, DiffType.DELETED).count());
  }

  @Test
  public void diffEngineShouldFollowAFixedOrderOfFinders() {
    assertEquals(EXPECTED_FINDER_ORDER, ramlDiffEngine.getFinders());
  }
}

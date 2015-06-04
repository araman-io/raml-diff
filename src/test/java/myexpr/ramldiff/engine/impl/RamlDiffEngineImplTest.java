package myexpr.ramldiff.engine.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.diff.DiffType;
import myexpr.ramldiff.engine.Finder;
import myexpr.ramldiff.engine.MockHelper;
import myexpr.ramldiff.engine.RamlDiffEngine;
import myexpr.ramldiff.engine.impl.RamlDiffEngineImpl;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithDifferingResponseSchemas;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithUpdatedQueryParameters;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithUpdatedResponseStatusCode;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithUpdatedTraits;
import myexpr.ramldiff.engine.impl.finders.FindNewActions;
import myexpr.ramldiff.engine.impl.finders.FindRemovedActions;

import org.junit.Before;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;

public class RamlDiffEngineImplTest extends MockHelper {

  Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
  List<Finder> EXPECTED_FINDER_ORDER = Arrays.asList(new FindNewActions(), new FindRemovedActions(),
      new FindActionsWithUpdatedQueryParameters(), new FindActionsWithUpdatedTraits(),
      new FindActionsWithUpdatedResponseStatusCode(), new FindActionsWithDifferingResponseSchemas());
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

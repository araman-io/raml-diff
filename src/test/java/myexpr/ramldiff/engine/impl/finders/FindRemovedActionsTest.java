package myexpr.ramldiff.engine.impl.finders;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.diff.DiffType;
import myexpr.ramldiff.engine.Finder;
import myexpr.ramldiff.engine.MockHelper;
import myexpr.ramldiff.engine.impl.finders.FindRemovedActions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.raml.model.Action;
import org.raml.model.ActionType;

@RunWith(MockitoJUnitRunner.class)
public class FindRemovedActionsTest extends MockHelper {

  Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
  private Finder removedActionFinder = new FindRemovedActions();

  @Before
  public void initTest() throws Exception {
    addMapEntryWith(newActionMap, ActionType.GET, "/foo");
    addMapEntryWith(newActionMap, ActionType.DELETE, "/foo");

    addMapEntryWith(oldActionMap, ActionType.GET, "/foo");
    addMapEntryWith(oldActionMap, ActionType.DELETE, "/foo/bar");
    addMapEntryWith(oldActionMap, ActionType.PATCH, "/foo/bar");
  }

  @Test
  public void shouldReport2DeletedActions() throws Exception {
    List<ActionDiff> diff = removedActionFinder.diff(newActionMap, oldActionMap);
    assertEquals(2, diff.size());
    assertEquals(DiffType.DELETED, super.getDistinctDiffType(diff));
    assertEquals(1, super.getDistinctDiffTypeCount(diff));

    assertEquals(new HashSet<ActionType>(Arrays.asList(ActionType.DELETE, ActionType.PATCH)),
        super.getSetOfActionTypes(diff));
  }

  @Test
  public void shouldReportNoDeletedActionsWhenSameActionMapIsUsed() throws Exception {
    List<ActionDiff> diff = removedActionFinder.diff(newActionMap, newActionMap);
    assertEquals(0, diff.size());
  }

  @Test
  public void shouldReportNoDeletedActionsWhenOldMapIsEmpty() throws Exception {
    List<ActionDiff> diff = removedActionFinder.diff(newActionMap, new HashMap<ActionId, Action>());
    assertEquals(0, diff.size());
  }

  @Test
  public void shouldReport3DeletedActionsWhenNewMapIsEmpty() throws Exception {
    List<ActionDiff> diff = removedActionFinder.diff(new HashMap<ActionId, Action>(), oldActionMap);
    assertEquals(3, diff.size());
    assertEquals(DiffType.DELETED, super.getDistinctDiffType(diff));
    assertEquals(new HashSet<ActionType>(Arrays.asList(ActionType.GET, ActionType.DELETE, ActionType.PATCH)),
        super.getSetOfActionTypes(diff));
  }

}

package engine.impl.finders;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.raml.model.Action;
import org.raml.model.ActionType;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import engine.Finder;
import engine.MockHelper;

@RunWith(MockitoJUnitRunner.class)
public class FindNewActionsTest extends MockHelper {

  Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
  private Finder newActionFinder;

  @Before
  public void initTest() throws Exception {
    addMapEntryWith(newActionMap, ActionType.GET, "/foo");
    addMapEntryWith(newActionMap, ActionType.DELETE, "/foo");

    addMapEntryWith(oldActionMap, ActionType.GET, "/foo");
    addMapEntryWith(oldActionMap, ActionType.DELETE, "/foo/bar");
    addMapEntryWith(oldActionMap, ActionType.PATCH, "/foo/bar");

    newActionFinder = new FindNewActions();
  }


  @Test
  public void shouldReport1NewAction() throws Exception {
    List<ActionDiff> diff = newActionFinder.diff(newActionMap, oldActionMap);
    assertEquals(1, diff.size());
    assertEquals(DiffType.NEW, diff.get(0).getDiffType());
    assertEquals(ActionType.DELETE, diff.get(0).getAction().getType());
  }

  @Test
  public void shouldReportNoNewActionsWhenSameActionMapIsUsed() throws Exception {
    newActionFinder = new FindNewActions();
    List<ActionDiff> diff = newActionFinder.diff(newActionMap, newActionMap);
    assertEquals(0, diff.size());
  }

  @Test
  public void shouldReportNoNewActionsWhenNewMapIsEmpty() throws Exception {
    newActionFinder = new FindNewActions();
    List<ActionDiff> diff = newActionFinder.diff(new HashMap<ActionId, Action>(), oldActionMap);
    assertEquals(0, diff.size());
  }

  @Test
  public void shouldReport2NewActionsWhenOldMapIsEmpty() throws Exception {
    newActionFinder = new FindNewActions();
    List<ActionDiff> diff = newActionFinder.diff(newActionMap, new HashMap<ActionId, Action>());
    assertEquals(2, diff.size());

    assertEquals(DiffType.NEW, getDistinctDiffType(diff));
    assertEquals(1, getDistinctDiffTypeCount(diff));
    assertEquals(new HashSet<ActionType>(Arrays.asList(ActionType.GET, ActionType.DELETE)), getSetOfActionTypes(diff));

  }
}

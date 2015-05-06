package engine.impl.finders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import engine.Finder;
import engine.MockHelper;

public class FindActionsWithUpdatedQueryParamsTest extends MockHelper {

  Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
  private Finder finderUnderTest = new FindActionsWithUpdatedQueryParameters();

  @Test
  public void shouldReturnOneAddedQueryParam() {
    addMapEntryWithQueryParamKeySet(newActionMap, ActionType.GET, "/foo",
        getSetFrom(Arrays.asList("query", "offset", "sort")));
    addMapEntryWithQueryParamKeySet(oldActionMap, ActionType.GET, "/foo", getSetFrom(Arrays.asList("query", "offset")));

    List<ActionDiff> diff = finderUnderTest.diff(newActionMap, oldActionMap);
    assertEquals(1, diff.size());
    assertEquals(DiffType.NEW, super.getDistinctDiffType(diff));
    assertEquals(1, super.getDistinctDiffTypeCount(diff));
  }

  @Test
  public void shouldReturnOneDeletedQueryParam() {
    addMapEntryWithQueryParamKeySet(newActionMap, ActionType.GET, "/foo",
        getSetFrom(Arrays.asList("query", "offset", "sort")));
    addMapEntryWithQueryParamKeySet(oldActionMap, ActionType.GET, "/foo",
        getSetFrom(Arrays.asList("query", "offset", "sort", "foobar")));

    List<ActionDiff> diff = finderUnderTest.diff(newActionMap, oldActionMap);
    assertEquals(1, diff.size());
    assertEquals(DiffType.DELETED, super.getDistinctDiffType(diff));
    assertEquals(1, super.getDistinctDiffTypeCount(diff));
  }

  @Test
  public void unrelatedActionsWithDifferentActionTypesShouldReturnNoDiffEvents() {
    addMapEntryWithQueryParamKeySet(newActionMap, ActionType.GET, "/foo",
        getSetFrom(Arrays.asList("query", "offset", "sort")));
    addMapEntryWithQueryParamKeySet(oldActionMap, ActionType.DELETE, "/foo",
        getSetFrom(Arrays.asList("query", "offset", "sort", "foobar")));

    List<ActionDiff> diff = finderUnderTest.diff(newActionMap, oldActionMap);
    assertEquals(0, diff.size());
  }

  @Test
  public void unrelatedActionsWithDifferentUrisShouldReturnNoDiffEvents() {
    addMapEntryWithQueryParamKeySet(newActionMap, ActionType.GET, "/foo",
        getSetFrom(Arrays.asList("query", "offset", "sort")));
    addMapEntryWithQueryParamKeySet(oldActionMap, ActionType.GET, "/foobar",
        getSetFrom(Arrays.asList("query", "offset", "sort", "foobar")));

    List<ActionDiff> diff = finderUnderTest.diff(newActionMap, oldActionMap);
    assertEquals(0, diff.size());
  }
  
  @Test
  public void shouldReturnOneNewAndOneDeleteDiffEvent() {
    addMapEntryWithQueryParamKeySet(newActionMap, ActionType.GET, "/foo",
        getSetFrom(Arrays.asList("query", "offset", "sort", "hasbeenadded")));
    addMapEntryWithQueryParamKeySet(oldActionMap, ActionType.GET, "/foo",
        getSetFrom(Arrays.asList("query", "offset", "sort", "tobedeleted")));

    List<ActionDiff> diff = finderUnderTest.diff(newActionMap, oldActionMap);
    assertEquals(2, diff.size());
    assertEquals(new HashSet<DiffType>(Arrays.asList(DiffType.NEW, DiffType.DELETED)), super.getSetOfDiffTypes(diff));
  }

  protected void addMapEntryWithQueryParamKeySet(Map<ActionId, Action> map, ActionType type, String uri,
      Set<String> paramSet) {
    ActionId id = new ActionId(type, uri);
    map.put(id, mockActionWith(type, uri, paramSet));
  }

  protected Action mockActionWith(ActionType type, String uri, Set<String> paramSet) {
    Action actionMock = mock(Action.class, RETURNS_DEEP_STUBS);
    when(actionMock.getType()).thenReturn(type);
    when(actionMock.getResource().getUri()).thenReturn(uri);
    when(actionMock.toString()).thenCallRealMethod();
    when(actionMock.getQueryParameters().keySet()).thenReturn(paramSet);
    return actionMock;
  }

  protected Set<String> getSetFrom(List<String> strings) {
    return strings.stream().collect(Collectors.toSet());
  }

}

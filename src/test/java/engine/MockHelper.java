package engine;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.raml.model.Action;
import org.raml.model.ActionType;

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

}

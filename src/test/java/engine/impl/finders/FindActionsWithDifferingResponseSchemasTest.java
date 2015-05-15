package engine.impl.finders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;

import diff.ActionId;
import engine.Finder;
import engine.MockHelper;

public class FindActionsWithDifferingResponseSchemasTest extends MockHelper {
  
  
  @Test
  public void instancesOfThisClassShouldBeEqual() {
    assertEquals(new FindActionsWithDifferingResponseSchemas(), new FindActionsWithDifferingResponseSchemas());
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

  
}
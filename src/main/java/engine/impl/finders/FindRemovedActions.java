package engine.impl.finders;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.model.Action;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import engine.Finder;

public class FindRemovedActions implements Finder {

  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    Collection<ActionId> deletedActionIds = CollectionUtils.subtract(oldActions.keySet(), newActions.keySet());

    List<ActionDiff> differences = deletedActionIds.stream().map(actionId -> {
      Action theAction = oldActions.get(actionId);
      return new ActionDiff(DiffType.DELETED, theAction);
    }).collect(Collectors.toList());

    return differences;
  }
  
  @Override
  public boolean equals(Object other) {
    boolean result = false;
   if ( getClass().equals(other.getClass())) {
     result = true;
   }
    return result;
  }

}

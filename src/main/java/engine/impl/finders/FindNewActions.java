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

public class FindNewActions  implements Finder {

  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    Collection<ActionId> newActionIds = CollectionUtils.subtract(newActions.keySet(), oldActions.keySet());

    List<ActionDiff> differences = newActionIds.stream().map(actionId -> {
      Action theAction = newActions.get(actionId);
      return new ActionDiff(DiffType.NEW, theAction);
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

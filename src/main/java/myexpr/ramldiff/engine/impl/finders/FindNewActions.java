package myexpr.ramldiff.engine.impl.finders;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.diff.DiffType;
import myexpr.ramldiff.engine.Finder;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.model.Action;

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

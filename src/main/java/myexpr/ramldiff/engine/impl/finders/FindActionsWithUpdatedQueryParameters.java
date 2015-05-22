package myexpr.ramldiff.engine.impl.finders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.diff.ActionParamDiff;
import myexpr.ramldiff.diff.DiffType;
import myexpr.ramldiff.engine.Finder;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.model.Action;

public class FindActionsWithUpdatedQueryParameters implements Finder {

  @Override
  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    Collection<ActionId> commonActionIds = CollectionUtils.intersection(newActions.keySet(), oldActions.keySet());

    List<ActionDiff> paramDiffEvents = commonActionIds.stream().flatMap(actionId -> {
      Action theAction = newActions.get(actionId);
      Set<String> oldQueryParams = getParameterKeySet(oldActions.get(actionId));
      Set<String> newQueryParams = getParameterKeySet(newActions.get(actionId));

      Collection<String> newParams = CollectionUtils.subtract(newQueryParams, oldQueryParams);
      Collection<String> deletedParams = CollectionUtils.subtract(oldQueryParams, newQueryParams);

      List<ActionDiff> diffEvents = new ArrayList<ActionDiff>();
      if (newParams.size() > 0 ) {
        diffEvents.add(new ActionParamDiff(DiffType.NEW, theAction, newParams));
      }
      
      if ( deletedParams.size() > 0 ) {
        diffEvents.add(new ActionParamDiff(DiffType.DELETED, theAction, deletedParams));
      }
      
      return diffEvents.stream();
    }).collect(Collectors.toList());

    return paramDiffEvents;
  }
  
  public Set<String> getParameterKeySet(Action action) {
    return action.getQueryParameters().keySet();
  }

  @Override
  public boolean equals(Object other) {
    boolean result = false;
    if (FindActionsWithUpdatedQueryParameters.class.equals(other.getClass())) {
      result = true;
    }
    return result;
  }

}

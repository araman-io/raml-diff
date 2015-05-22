package myexpr.ramldiff.engine.impl.finders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.diff.DiffType;
import myexpr.ramldiff.diff.ResponseStatusCodeDiff;
import myexpr.ramldiff.engine.Finder;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.model.Action;

/**
 * The class finds out the Responses which were added and/or deleted from an action by comparing the
 * status code.
 */
public class FindActionsWithUpdatedResponseStatusCode implements Finder {

  /**
   * The method returns the list of ActionDiff objects indicating the following : 1. Response
   * Objects that are ADDED 2. Response Objects that are DELETED
   * 
   * @param Map<ActionId, Action>
   * @param Map<ActionId, Action>
   * @return List<ActionDiff>
   */
  @Override
  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    List<ActionDiff> actionsWithDifferingResponseStatusCodes = null;
    Collection<ActionId> commonActions = CollectionUtils.intersection(newActions.keySet(), oldActions.keySet());
    actionsWithDifferingResponseStatusCodes = commonActions.stream().flatMap(actionId -> {

      Action commonAction = newActions.get(actionId);
      Set<String> newActionResponses = retrieveResponseKeyset(newActions, actionId);
      Set<String> oldActionResponses = retrieveResponseKeyset(oldActions, actionId);

      Collection<String> newStatusCodes = CollectionUtils.subtract(newActionResponses, oldActionResponses);
      Collection<String> deletedStatusCodes = CollectionUtils.subtract(oldActionResponses, newActionResponses);

      List<ActionDiff> allDifferences = new ArrayList<ActionDiff>();
      
      if (CollectionUtils.isNotEmpty(newStatusCodes)) {
        newStatusCodes.stream().forEach(statusCode -> {
          allDifferences.add(new ResponseStatusCodeDiff(DiffType.NEW, commonAction, statusCode));
        });
      }

      if (CollectionUtils.isNotEmpty(deletedStatusCodes)) {
        deletedStatusCodes.stream().forEach(statusCode -> {
          allDifferences.add(new ResponseStatusCodeDiff(DiffType.DELETED, commonAction, statusCode));
        });
      }

      return allDifferences.stream();
    }).collect(Collectors.toList());
    
    return actionsWithDifferingResponseStatusCodes;
  }

  /**
   * The method returns all the responses associated with a corresponding ActionId
   * 
   * @param Map<ActionId, Action>
   * @param ActionId
   * @return Set<String>
   */
  protected Set<String> retrieveResponseKeyset(Map<ActionId, Action> actionMap, ActionId actionId) {
    return actionMap.get(actionId).getResponses().keySet();
  }

  /**
   * all instances of this class are equal
   */
  public boolean equals(Object o) {
    boolean result = false;
    if (FindActionsWithUpdatedResponseStatusCode.class.getName().equals(o.getClass().getName())) {
      result = true;
    }
    return result;
  }

}

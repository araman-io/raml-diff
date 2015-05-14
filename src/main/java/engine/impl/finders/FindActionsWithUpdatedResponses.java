package engine.impl.finders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.model.Action;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import diff.ResponseDiff;
import engine.Finder;

/**
 * The class finds out the Response that are added and/or deleted.
 * 
 *
 */
public class FindActionsWithUpdatedResponses implements Finder {

  /**
   * The method returns the list of ActionDiff objects indicating the following : 
   * 1. Response Objects that are ADDED 
   * 2. Response Objects that are DELETED
   * 
   * 
   * @param Map<ActionId, Action>
   * @param Map<ActionId, Action>
   * @return List<ActionDiff>
   * 
   */
  @Override
  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    List<ActionDiff> actionsWithDifferentResponseSchemas = null;
    Collection<ActionId> commonActions = CollectionUtils.intersection(newActions.keySet(), oldActions.keySet());
    actionsWithDifferentResponseSchemas =
        commonActions
            .stream()
            .flatMap(
                actionId -> {

                  Action commonAction = newActions.get(actionId);
                  Set<String> newActionResponses = retrieveResponseKeyset(newActions, actionId);
                  Set<String> oldActionResponses = retrieveResponseKeyset(oldActions, actionId);

                  Collection<String> addedResponseKeySet =
                      CollectionUtils.subtract(newActionResponses, oldActionResponses);
                  Collection<String> deletedResponseKeySet =
                      CollectionUtils.subtract(oldActionResponses, newActionResponses);

                  List<ActionDiff> allDifferences = new ArrayList<ActionDiff>();
                  if (CollectionUtils.isNotEmpty(addedResponseKeySet)) {
                    allDifferences.add(new ResponseDiff(DiffType.NEW, commonAction, addedResponseKeySet));
                  }

                  if (CollectionUtils.isNotEmpty(deletedResponseKeySet)) {
                    allDifferences.add(new ResponseDiff(DiffType.DELETED, commonAction, deletedResponseKeySet));
                  }

                  return allDifferences.stream();
                }).collect(Collectors.toList());
    return actionsWithDifferentResponseSchemas;
  }

  /**
   * The method returns all the responses associated with a corresponding ActionId
   * 
   * @param Map<ActionId, Action>
   * @param ActionId
   * @return Set<String>
   */
  public Set<String> retrieveResponseKeyset(Map<ActionId, Action> actionMap, ActionId actionId) {
    return actionMap.get(actionId).getResponses().keySet();
  }

  public boolean equals(Object o) {
    boolean result = false;
    if (FindActionsWithUpdatedResponses.class.getName().equals(o.getClass().getName())) {
      result = true;
    }

    return result;
  }
}
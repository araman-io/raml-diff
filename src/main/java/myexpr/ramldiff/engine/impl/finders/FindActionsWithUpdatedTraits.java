package myexpr.ramldiff.engine.impl.finders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.diff.DiffType;
import myexpr.ramldiff.diff.TraitDiff;
import myexpr.ramldiff.engine.Finder;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.model.Action;

/**
 * Find actions with New and/or Deleted Traits.
 */
public class FindActionsWithUpdatedTraits implements Finder {

  @Override
  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    List<ActionDiff> traitDifferences = null;

    Collection<ActionId> commonActionIds = CollectionUtils.intersection(newActions.keySet(), oldActions.keySet());

    traitDifferences = commonActionIds.stream().flatMap(actionId -> {
      Action commonAction = newActions.get(actionId);
      List<String> traitsInNewRamlFile = commonAction.getIs();
      List<String> traitsInOldRamlFile = oldActions.get(actionId).getIs();

      Collection<String> traitsAdded = CollectionUtils.subtract(traitsInNewRamlFile, traitsInOldRamlFile);
      Collection<String> traitsDeleted = CollectionUtils.subtract(traitsInOldRamlFile, traitsInNewRamlFile);

      List<ActionDiff> traitDetails = new ArrayList<ActionDiff>();
      if (CollectionUtils.isNotEmpty(traitsAdded)) {
        traitDetails.add(new TraitDiff(DiffType.NEW, commonAction, traitsAdded));
      }

      if (CollectionUtils.isNotEmpty(traitsDeleted)) {
        traitDetails.add(new TraitDiff(DiffType.DELETED, commonAction, traitsDeleted));
      }

      return traitDetails.stream();

    }).collect(Collectors.toList());

    return traitDifferences;
  }

  public boolean equals(Object o) {
    boolean result = false;
    if (o.getClass().getName().equals(FindActionsWithUpdatedTraits.class.getName())) {
      result = true;
    }

    return result;
  }
}

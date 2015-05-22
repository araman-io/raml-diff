package myexpr.ramldiff.engine.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.engine.Finder;
import myexpr.ramldiff.engine.RamlDiffEngine;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithDifferingResponseSchemas;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithUpdatedQueryParameters;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithUpdatedResponseStatusCode;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithUpdatedTraits;
import myexpr.ramldiff.engine.impl.finders.FindNewActions;
import myexpr.ramldiff.engine.impl.finders.FindRemovedActions;

import org.raml.model.Action;

public class RamlDiffEngineImpl implements RamlDiffEngine {

  protected List<Finder> finders = Arrays.asList(new FindNewActions(), new FindRemovedActions(),
      new FindActionsWithUpdatedQueryParameters(), new FindActionsWithUpdatedTraits(),
      new FindActionsWithUpdatedResponseStatusCode(), new FindActionsWithDifferingResponseSchemas());

  public List<ActionDiff> findDifferences(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {

    //@formatter:off
    List<ActionDiff> allDifferences = 
        getFinders()
          .stream()
          .flatMap(finder -> {
              return finder.diff(newActions, oldActions).stream();
          })
          .collect(Collectors.toList());
    //@formatter:on

    return allDifferences;
  }

  @Override
  public List<Finder> getFinders() {
    return finders;
  }
}

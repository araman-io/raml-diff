package engine.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.raml.model.Action;

import diff.ActionDiff;
import diff.ActionId;
import engine.Finder;
import engine.RamlDiffEngine;
import engine.impl.finders.FindActionsWithUpdatedQueryParameters;
import engine.impl.finders.FindActionsWithUpdatedUriParameters;
import engine.impl.finders.FindNewActions;
import engine.impl.finders.FindRemovedActions;

public class RamlDiffEngineImpl implements RamlDiffEngine {

  protected List<Finder> finders = Arrays.asList(
      new FindNewActions(), 
      new FindRemovedActions(),
      new FindActionsWithUpdatedQueryParameters(),
      new FindActionsWithUpdatedUriParameters());

  public List<ActionDiff> findDifferences(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {

    List<ActionDiff> allDifferences = finders.stream().flatMap(finder -> {
      return finder.diff(newActions, oldActions).stream();
    }).collect(Collectors.toList());

    return allDifferences;

  }

  @Override
  public List<Finder> getFinders() {
    return finders;
  }
}

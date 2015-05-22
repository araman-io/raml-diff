package myexpr.ramldiff.engine;

import java.util.List;
import java.util.Map;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;

import org.raml.model.Action;

public interface RamlDiffEngine {
  
  public List<ActionDiff> findDifferences(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions);

  public List<Finder> getFinders();

}

package engine;

import java.util.List;
import java.util.Map;

import org.raml.model.Action;

import diff.ActionDiff;
import diff.ActionId;

public interface RamlDiffEngine {
  
  public List<ActionDiff> findDifferences(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions);

  public List<Finder> getFinders();

}

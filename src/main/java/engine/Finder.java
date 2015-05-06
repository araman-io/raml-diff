package engine;

import java.util.List;
import java.util.Map;

import org.raml.model.Action;

import diff.ActionDiff;
import diff.ActionId;

public interface Finder {

  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions);

}

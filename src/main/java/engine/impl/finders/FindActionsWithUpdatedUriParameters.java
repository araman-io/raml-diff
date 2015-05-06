package engine.impl.finders;

import java.util.Set;

import org.raml.model.Action;

import engine.Finder;

public class FindActionsWithUpdatedUriParameters extends AbstractFindActionsWithUpdatedParameters implements Finder {

  public Set<String> getParameterKeySet(Action action) {
    return action.getResource().getUriParameters().keySet();
  }

  @Override
  public boolean equals(Object other) {
    boolean result = false;
    if (getClass().equals(other.getClass())) {
      result = true;
    }
    return result;
  }

}

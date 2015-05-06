package diff;

import java.util.Collection;

import org.raml.model.Action;

public class ActionParamDiff extends ActionDiff {

  Collection<String> parameters;

  public ActionParamDiff(DiffType diffType, Action action, Collection<String> parameters) {
    super(diffType, action);
    this.parameters = parameters;
  }
  
  @Override
  public String toString() {
    return "Parameters " + parameters + " is " + this.getDiffType() + " in action " + this.getAction();
  }


}

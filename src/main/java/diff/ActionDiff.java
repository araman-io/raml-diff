package diff;

import org.raml.model.Action;


public class ActionDiff {

  DiffType diffType;
  Action action;

  public ActionDiff(DiffType diffType, Action action) {
    super();
    this.diffType = diffType;
    this.action = action;
  }

  public DiffType getDiffType() {
    return diffType;
  }

  public Action getAction() {
    return action;
  }
  
  @Override
  public String toString() {
    return this.getAction() + " is " + this.getDiffType();
  }
}

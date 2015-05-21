package diff;

import java.util.HashMap;
import java.util.Map;

import org.raml.model.Action;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;


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
  
  public Map<String, Object> getState() {
    Map<String, Object> state = new HashMap<String, Object>();
    state.put("DiffType", diffType);
    state.put("action", new ActionId(action));
    
    return state;
  }
  
  @Override
  public String toString() {
    return this.getAction() + " is " + this.getDiffType();
  }
}

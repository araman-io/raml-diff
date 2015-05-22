package myexpr.ramldiff.diff;

import java.util.Map;

import org.raml.model.Action;

public class ResponseStatusCodeDiff extends ActionDiff {

  String statusCode;

  public ResponseStatusCodeDiff(DiffType diffType, Action action, String statusCode) {
    super(diffType, action);
    this.statusCode = statusCode;
  }


  public String toString() {
    return "Status Code " + statusCode + " is " + this.getDiffType() + " in " + this.getAction();
  }
  
  @Override
  public Map<String, Object> getState() {
    Map<String, Object> state = super.getState();
    state.put("statusCode", statusCode);
    return state;
  }

}

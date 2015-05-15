package diff;

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

}

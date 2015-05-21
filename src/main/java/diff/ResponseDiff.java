package diff;

import java.util.Collection;
import java.util.Map;

import org.raml.model.Action;

public class ResponseDiff extends ActionDiff {
  
  Collection<String> responses;
  
  public ResponseDiff(DiffType diffType, Action action, Collection<String> responses){
    super(diffType, action);
    this.responses = responses;
  }
  
  
  public String toString(){
    return "Response "+responses+" is " + this.getDiffType() + " in " + this.getAction();
  }
  
  @Override
  public Map<String, Object> getState() {
    Map<String, Object> state = super.getState();
    state.put("responses", responses);
    return state;
  }
  
}

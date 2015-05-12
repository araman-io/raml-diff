package service;

import org.apache.commons.lang.StringUtils;
import org.raml.model.Action;

import diff.ActionId;

public class ActionIdService {

  public static ActionId getId(Action action) {
	String resourceUri = null;
	if(action.getResource() != null && StringUtils.isNotBlank(action.getResource().getParentUri())){
		resourceUri = action.getResource().getParentUri() + action.getResource().getRelativeUri();
	}else{
		resourceUri = action.getResource().getRelativeUri();
	}
    ActionId id = new ActionId(action.getType(), resourceUri);
    return id;
  }

  public static Action getAction(Action action) {
    return action;
  }

  
}

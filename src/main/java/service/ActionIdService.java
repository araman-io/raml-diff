package service;

import org.raml.model.Action;

import diff.ActionId;

public class ActionIdService {

  public static ActionId getId(Action action) {
    ActionId id = new ActionId(action.getType(), action.getResource().getRelativeUri());
    return id;
  }

  public static Action getAction(Action action) {
    return action;
  }

  
}

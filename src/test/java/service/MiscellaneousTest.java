package service;

import java.util.Collection;
import java.util.Map;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;

import diff.ActionId;

public class MiscellaneousTest extends RamlDiffService {

//  @Test
  public void whatDoesActionDotResponsesContain() throws Exception {
    RamlDiffService ramlDiffService = new RamlDiffService();
    Collection<Resource> ramlResourcesFor =
        ramlDiffService.getRamlResourcesFor("d:/gitrepo/api-console/src/assets/examples/github.raml");
    Map<ActionId, Action> ramlActionsFor = ramlDiffService.getRamlActionsFor(ramlResourcesFor);

    ActionId id = new ActionId(ActionType.PUT, "/teams/{teamsId}/members/{userId}");
    
    Action action = ramlActionsFor.get(id);
    
    System.out.println(action);
    
  }

}

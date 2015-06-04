package myexpr.ramldiff.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.service.RamlDiffService;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;

import com.google.gson.Gson;

public class MiscellaneousTest extends RamlDiffService {

  // @Test
  public void whatDoesActionDotResponsesContain() throws Exception {
    RamlDiffService ramlDiffService = new RamlDiffService();
    Collection<Resource> ramlResourcesFor =
        ramlDiffService.getRamlResourcesFor("d:/gitrepo/api-console/src/assets/examples/github.raml");
    Map<ActionId, Action> ramlActionsFor = ramlDiffService.getRamlActionsFor(ramlResourcesFor);

    ActionId id = new ActionId(ActionType.PUT, "/teams/{teamsId}/members/{userId}");

    Action action = ramlActionsFor.get(id);

    System.out.println(action);

  }

//  @Test
  public void testGson() throws Exception {
    RamlDiffService ramlDiffService = new RamlDiffService();
    List<ActionDiff> diff = ramlDiffService.diff("d:/gitrepo/raml-diff/src/test/resources/github.raml",
        "d:/gitrepo/raml-diff/src/test/resources/github-api-v3.raml");
    
    Gson gson = new Gson();
    
    String collect = diff.stream().map(d -> {
      return gson.toJson(d.getState());
    }).collect(Collectors.joining(",", "[", "]"));
    
    System.out.println(collect);
  }
}

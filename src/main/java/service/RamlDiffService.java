package service;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.raml.model.Action;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.visitor.RamlDocumentBuilder;

import diff.ActionId;

public class RamlDiffService {

  public void diff(String later, String older) throws Exception {

    Collection<Resource> laterResources = getRamlResourcesFor(later);
    Collection<Resource> olderResources = getRamlResourcesFor(older);

    System.out.println("number of resources in this document " + laterResources.size());

    Map<ActionId, Action> laterActions = getRamlActionsFor(laterResources);
    Map<ActionId, Action> olderActions = getRamlActionsFor(olderResources);

    System.out.println(laterActions);
    System.out.println(olderActions);

    System.out.println("list of actions in this document " + laterActions.size());

  }

  public Map<ActionId, Action> getRamlActionsFor(Collection<Resource> resources) {
    return resources.stream().flatMap(resource -> {
      return resource.getActions().values().stream();
    }).limit(2).collect(Collectors.toMap(ActionIdService::getId, ActionIdService::getAction));
  }

  protected Collection<Resource> getRamlResourcesFor(String fileName) throws Exception {
    FileReader fileReader = new FileReader(new File(fileName));
    Raml document = new RamlDocumentBuilder().build(fileReader);
    Collection<Resource> allResources = this.flattenResources(document.getResources().values());
    return allResources;
  }

  protected Collection<Resource> flattenResources(Collection<Resource> resources) {
    Collection<Resource> nested = new ArrayList<Resource>();
    for (Resource r : resources) {
      nested.add(r);
      if (r.getResources().values().size() > 0) {
        nested.addAll(flattenResources(r.getResources().values()));
      }
    }
    return nested;
  }

  public static void main(String[] args) throws Exception {
    new RamlDiffService().diff("d:/gitrepo/api-console/src/assets/examples/github.raml",
        "d:/gitrepo/api-console/src/assets/examples/box.raml");
  }

}

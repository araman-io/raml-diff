package service;

import static spark.Spark.get;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.raml.model.Action;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.visitor.RamlDocumentBuilder;

import diff.ActionDiff;
import diff.ActionId;
import engine.RamlDiffEngine;
import engine.impl.RamlDiffEngineImpl;

public class RamlDiffService {

  private static final String RAML_DIFF_SERVICE_AVAILABLE_TEXT = "Raml Diff Service is up and running";
  private static final String RAML_DIFF_SERVICE = "/ramlDiffService";
  private static final String FIND_ALL_DIFFERENCES_CONTEXT = "/findAllDifferences";
  
  RamlDiffEngine diffEngine = new RamlDiffEngineImpl();

  public List<ActionDiff> diff(String later, String older) throws Exception {
    Collection<Resource> laterResources = getRamlResourcesFor(later);
    Collection<Resource> olderResources = getRamlResourcesFor(older);


    Map<ActionId, Action> mapOfNewActions = getRamlActionsFor(laterResources);
    Map<ActionId, Action> mapOfOldActions = getRamlActionsFor(olderResources);
    List<ActionDiff> allDifferences = diffEngine.findDifferences(mapOfNewActions, mapOfOldActions);

    allDifferences.forEach(diff -> {
      System.out.println(diff.toString());
    });

    return allDifferences;
  }

  protected Map<ActionId, Action> getRamlActionsFor(Collection<Resource> resources) {
    return resources.stream().flatMap(resource -> {
      return resource.getActions().values().stream();
    }).collect(Collectors.toMap(ActionIdService::getId, ActionIdService::getAction));
  }

  protected Collection<Resource> getRamlResourcesFor(String fileName) throws Exception {
    FileReader fileReader = new FileReader(new File(fileName));
    @SuppressWarnings("deprecation")
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

  /*
   * public static void main(String[] args) throws Exception { new RamlDiffService().diff(
   * "src/test/resources/04-bookservice-addqueryparam.raml",
   * "src/test/resources/01-bookservice.raml"); }
   */

  /*
   * public static void main(String[] args) throws Exception { new
   * RamlDiffService().diff("src/test/resources/github.raml",
   * "src/test/resources/github-api-v3.raml"); }
   */

  public static void main(String[] args) {
    get(RAML_DIFF_SERVICE, (request, response) -> {
      return RAML_DIFF_SERVICE_AVAILABLE_TEXT;
    });
    get(FIND_ALL_DIFFERENCES_CONTEXT, (request, response) -> {

      List<ActionDiff> allDifferences = null;
      String oldRamlFilePath = request.queryParams("oldRamlFileURL");
      String newRamlFilePath = request.queryParams("newRamlFileURL");

      URL oldFileUrl = new URL(oldRamlFilePath);
      URL newFileUrl = new URL(newRamlFilePath);
      try {
        allDifferences = new RamlDiffService().diff(newFileUrl.getFile(), oldFileUrl.getFile());
      } catch (Exception e) {
        e.printStackTrace();
      }
      return allDifferences;
    });
  }
}

/*
 * "src/test/resources/05-bookservice-addbaseuriparam.raml",
 * "src/test/resources/04-bookservice-addqueryparam.raml",
 * "src/test/resources/03-bookservice-removedqueryparam.raml",
 * "src/test/resources/02-bookservice-newaction.raml", "src/test/resources/01-bookservice.raml"
 */

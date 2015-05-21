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

import com.google.gson.Gson;

import diff.ActionDiff;
import diff.ActionId;
import engine.RamlDiffEngine;
import engine.impl.RamlDiffEngineImpl;

public class RamlDiffService {

  RamlDiffEngine diffEngine = new RamlDiffEngineImpl();
  public static RamlDiffService SERVICE_INSTANCE = new RamlDiffService();

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

  public static void main(String[] args) {
    get("/", (request, response) -> {
      return "RAML diff service is available. \n" + "/findDiff?v1=<<fileURL>>&v2=<<fileURL>>";
    });

    get("/findDiff", (request, response) -> {

      List<ActionDiff> allDifferences = null;
      String oldRamlFilePath = request.queryParams("v1");
      String newRamlFilePath = request.queryParams("v2");

      URL oldFileUrl = new URL(oldRamlFilePath);
      URL newFileUrl = new URL(newRamlFilePath);
      allDifferences = SERVICE_INSTANCE.diff(newFileUrl.getFile(), oldFileUrl.getFile());

      Gson gson = new Gson();

      //@formatter:off
      String collect = 
          allDifferences.stream()
          .map(d -> gson.toJson(d.getState()))
          .collect(Collectors.joining(",", "[", "]"));
      //@formatter:on

      return collect;
    });
  }
}

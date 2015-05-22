package myexpr.ramldiff.service;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.engine.RamlDiffEngine;
import myexpr.ramldiff.engine.impl.RamlDiffEngineImpl;

import org.raml.model.Action;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.visitor.RamlDocumentBuilder;

public class RamlDiffService {

  RamlDiffEngine diffEngine = new RamlDiffEngineImpl();

  public List<ActionDiff> diff(String later, String older) throws Exception {
    Collection<Resource> laterResources = getRamlResourcesFor(later);
    Collection<Resource> olderResources = getRamlResourcesFor(older);


    Map<ActionId, Action> mapOfNewActions = getRamlActionsFor(laterResources);
    Map<ActionId, Action> mapOfOldActions = getRamlActionsFor(olderResources);
    List<ActionDiff> allDifferences = diffEngine.findDifferences(mapOfNewActions, mapOfOldActions);

    return allDifferences;
  }

  protected Map<ActionId, Action> getRamlActionsFor(Collection<Resource> resources) {
    return resources.stream().flatMap(resource -> {
      return resource.getActions().values().stream();
    }).collect(Collectors.toMap(a -> new ActionId(a), a -> a));
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

}

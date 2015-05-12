package service;

import java.io.File;
import java.io.FileReader;
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

  RamlDiffEngine diffEngine = new RamlDiffEngineImpl();

  public void diff(String later, String older) throws Exception {

    Collection<Resource> laterResources = getRamlResourcesFor(later);
    Collection<Resource> olderResources = getRamlResourcesFor(older);

    Map<ActionId, Action> mapOfNewActions = getRamlActionsFor(laterResources);
    Map<ActionId, Action> mapOfOldActions = getRamlActionsFor(olderResources);

    List<ActionDiff> allDifferences = diffEngine.findDifferences(mapOfNewActions, mapOfOldActions);

    allDifferences.forEach(diff -> {
      System.out.println(diff.toString());
    });
  }

  public Map<ActionId, Action> getRamlActionsFor(Collection<Resource> resources) {
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

  /*public static void main(String[] args) throws Exception {
    new RamlDiffService().diff(
        "src/test/resources/04-bookservice-addqueryparam.raml",   
        "src/test/resources/01-bookservice.raml");
  }*/
  
  public static void main(String[] args) throws Exception {
	    new RamlDiffService().diff(
	    	"src/test/resources/complete_raml.raml",
	    	"src/test/resources/ complete_raml_with_one_parameter_added_to_historical_trait.raml"
	        );
	  }

}

/*
"src/test/resources/05-bookservice-addbaseuriparam.raml",
"src/test/resources/04-bookservice-addqueryparam.raml",
"src/test/resources/03-bookservice-removedqueryparam.raml",
"src/test/resources/02-bookservice-newaction.raml",          
"src/test/resources/01-bookservice.raml"
*/
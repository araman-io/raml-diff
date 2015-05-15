package engine.impl;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;

import diff.ActionDiff;
import diff.ActionId;
import diff.ActionParamDiff;
import diff.TraitDiff;
import engine.Finder;
import engine.MockHelper;
import engine.RamlDiffEngine;
import engine.impl.finders.FindActionsWithUpdatedTraits;

public class RamlDiffEngineImplWithOnlyTraitFinderTest extends MockHelper {
  
  Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
  List<Finder> EXPECTED_FINDER_ORDER = Arrays.asList(new FindActionsWithUpdatedTraits());
  
  private RamlDiffEngine classToTest = new RamlDiffEngineImpl();
  
  @Before
  public void init(){
    List<String> traits = new ArrayList<String>();
    traits.add("historical");    
    addMapEntryWithTraits(newActionMap, ActionType.GET, "/foo", traits);    
    traits = new ArrayList<String>();    
    addMapEntryWithTraits(oldActionMap, ActionType.GET, "/foo", traits);
    
  }
  
  @Test
  public void reportsOneAdditionOfTraitsWhenOneTraitIsAddedToNewRamlFile(){
    List<ActionDiff> traitDifferences = classToTest.findDifferences(newActionMap, oldActionMap);
    assertEquals(1, traitDifferences.size());
  }
  
  @Test
  public void reportsOneDeletionOfTraitsWhenOneTraitIsRemovedFromNewRamlFile(){
    List<ActionDiff> traitDifferences = classToTest.findDifferences(oldActionMap, newActionMap);
    assertEquals(1, traitDifferences.size());
    
  }
  
  @Test
  public void reportsNoChangeInTraitInformationWhenThereAreNoChangesInRamlFile(){
    List<ActionDiff> traitDifferences = classToTest.findDifferences(oldActionMap, oldActionMap);
    assertEquals(0, traitDifferences.size());
    traitDifferences = classToTest.findDifferences(newActionMap, newActionMap);
    assertEquals(0, traitDifferences.size());
  }
  
  @Test
  public void reportsAdditionOfNewParameterWhenParameterIsAddedToTrait() throws FileNotFoundException{
    String oldRamlFilePath = "src/test/resources/complete_raml_with_one_resource_with_trait.raml";
    String newRamlFilePath = "src/test/resources/complete_raml_with_one_resource_with_trait_one_parameter_added_to_trait.raml";
    Collection<Resource> oldRamlResources = retrieveResources(oldRamlFilePath);
    Map<ActionId, Action> oldActionMap = getRamlActionsFor(oldRamlResources);
    
    Collection<Resource> newRamlResources = retrieveResources(newRamlFilePath);
    Map<ActionId, Action> newActionMap = getRamlActionsFor(newRamlResources);
    
    List<ActionDiff> allDifferences = classToTest.findDifferences(newActionMap, oldActionMap);
    int countOfTraits = 0;
    int countOfParams = 0;
    for(ActionDiff difference : allDifferences){
      if(difference instanceof ActionParamDiff){
        countOfParams+=1;
      }else if(difference instanceof TraitDiff){
        countOfTraits+=1;
      }
    }
    
    assertEquals(1, allDifferences.size());
    assertEquals(1, countOfParams);
    assertEquals(0, countOfTraits);    
  }
}
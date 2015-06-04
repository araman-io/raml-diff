package myexpr.ramldiff.engine.impl.finders;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.diff.DiffType;
import myexpr.ramldiff.engine.Finder;
import myexpr.ramldiff.engine.MockHelper;
import myexpr.ramldiff.engine.impl.finders.FindActionsWithUpdatedTraits;

import org.junit.Before;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;

public class FindActionsWithUpdatedTraitsTest extends MockHelper {
  
  Map<ActionId, Action> newActionMap = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActionMap = new HashMap<ActionId, Action>();
  List<Finder> EXPECTED_FINDER_ORDER = Arrays.asList(new FindActionsWithUpdatedTraits());
  
  private FindActionsWithUpdatedTraits updatedTraitsFinder = new FindActionsWithUpdatedTraits();
  
  @Before
  public void init(){
    List<String> traits = new ArrayList<String>();
    traits.add("historical");
    
    addMapEntryWithTraits(newActionMap, ActionType.GET, "/foo", traits);    
    traits = new ArrayList<String>();
    
    addMapEntryWithTraits(newActionMap, ActionType.DELETE, "/foo", traits);

    addMapEntryWithTraits(oldActionMap, ActionType.GET, "/foo", traits);
    addMapEntryWithTraits(oldActionMap, ActionType.DELETE, "/foo/bar", traits);
    addMapEntryWithTraits(oldActionMap, ActionType.PATCH, "/foo/bar", traits);
  }
  
  @Test
  public void shouldReportOneTraitAddedIfOneTraitIsAddedToNewRAMLFile(){
    List<ActionDiff> differingTraits =  updatedTraitsFinder.diff(newActionMap, oldActionMap);
    int countOfTraitAdditions = 0;
    for(ActionDiff differingTrait : differingTraits){
      if(DiffType.NEW.equals(differingTrait.getDiffType())){
        countOfTraitAdditions++;
      }
    }
    assertEquals(1, countOfTraitAdditions);
  }
  
  @Test
  public void shouldReportOneTraitDeletedIfOneTraitIsRemovedFromNewRamlFile(){
    List<ActionDiff> differingTraits =  updatedTraitsFinder.diff(oldActionMap, newActionMap);
    int countOfTraitAdditions = 0;
    for(ActionDiff differingTrait : differingTraits){
      if(DiffType.DELETED.equals(differingTrait.getDiffType())){
        countOfTraitAdditions++;
      }
    }
    assertEquals(1, countOfTraitAdditions);
  }
  
  @Test
  public void shouldReportNoChangeInTraitIfThereIsNoChangeInRAMLFiles(){
    List<ActionDiff> differingTraits =  updatedTraitsFinder.diff(oldActionMap, oldActionMap);
    int countOfTraitAdditions = 0;
    for(ActionDiff differingTrait : differingTraits){
      if(DiffType.DELETED.equals(differingTrait.getDiffType())){
        countOfTraitAdditions++;
      }
    }
    assertEquals(0, countOfTraitAdditions);
  }
  
  

}

package engine.impl.finders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.raml.model.Action;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import diff.TraitDiff;
import engine.Finder;

public class FindActionsWithUpdatedTraits implements Finder{

	/**
	 * The method provides the list of ActionDiffs with New and/or Deleted Traits.
	 */
	@Override
	public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
		List<ActionDiff> traitDifferenceDetails = null;
		if(MapUtils.isNotEmpty(newActions) && MapUtils.isNotEmpty(oldActions)){
		  
			Collection<ActionId> commonActionIds = CollectionUtils.intersection(newActions.keySet(), oldActions.keySet());
			
			traitDifferenceDetails = commonActionIds.stream().flatMap(actionId -> {
				Action commonAction = newActions.get(actionId);
				List<String> traitsInNewRamlFile = retrieveTraitDetails(newActions, actionId);
				List<String> traitsInOldRamlFile = retrieveTraitDetails(oldActions, actionId);
				
				Collection<String> traitsAdded = CollectionUtils.subtract(traitsInNewRamlFile, traitsInOldRamlFile);
				Collection<String> traitsDeleted = CollectionUtils.subtract(traitsInOldRamlFile, traitsInNewRamlFile);
				
				List<ActionDiff> traitDetails = new ArrayList<ActionDiff>();
				if(CollectionUtils.isNotEmpty(traitsAdded)){
					traitDetails.add(new TraitDiff(DiffType.NEW, commonAction, traitsAdded));
				}
				
				if(CollectionUtils.isNotEmpty(traitsDeleted)){
					traitDetails.add(new TraitDiff(DiffType.DELETED, commonAction, traitsDeleted));
				}
				
				return traitDetails.stream();
			}).collect(Collectors.toList());
		}		
		return traitDifferenceDetails;
	}

	/**
	 * The method retrieves the Traits from Action.
	 * 
	 * @param Map<ActionId, Action>
	 * @param ActionId
	 * @return List<String>
	 */
	private List<String> retrieveTraitDetails(Map<ActionId, Action> actions, ActionId actionId) {
		List<String> traitDetails = null;
		Action action = actions.get(actionId);
		if(action != null){
			traitDetails = action.getIs();
		}
		return traitDetails;
	}
}
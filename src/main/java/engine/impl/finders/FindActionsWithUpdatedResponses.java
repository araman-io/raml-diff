package engine.impl.finders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Response;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import diff.ResponseDiff;
import engine.Finder;

/**
 * The class finds out the Response that are added, deleted or modified. 
 * 
 *
 */
public class FindActionsWithUpdatedResponses implements Finder {

  private static final String RESPONSE = "Response :";
  private static final String IS_UPDATED_TEXT = " is updated";
  private static final String IS_DELETED_TEXT = " is deleted";
  private static final String IS_ADDED_TEXT = " is added";
  private static final String MIME_TYPE = " - Mime Type - ";



  /**
   * The method returns the list of ActionDiff objects indicating the following :
   * 1. Response Objects that are ADDED
   * 2. Response Objects that are DELETED
   * 3. Response Objects whose Schema has been modified as UPDATED.
   * 
   * @param Map<ActionId, Action>
   * @param Map<ActionId, Action>
   * @return List<ActionDiff>
   *  
   */
  @Override
  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    List<ActionDiff> actionsWithDifferentResponseSchemas = null;
    Collection<ActionId> commonActions = CollectionUtils.intersection(newActions.keySet(), oldActions.keySet());
    actionsWithDifferentResponseSchemas = commonActions.stream().flatMap(actionId -> {
     
      Action commonAction = newActions.get(actionId);
      Set<String> newActionResponses = retrieveResponseKeyset(newActions, actionId);      
      Set<String> oldActionResponses = retrieveResponseKeyset(oldActions, actionId);
      
      Collection<String> addedResponseKeySet = CollectionUtils.subtract(newActionResponses, oldActionResponses);      
      Collection<String> deletedResponseKeySet = CollectionUtils.subtract(oldActionResponses, newActionResponses); 
      Collection<String> commonResponseKeySet = CollectionUtils.intersection(newActionResponses, oldActionResponses);
      
      Map<String, Response> newResponses = retrieveResponses(newActions, actionId);
      Map<String, Response> oldResponses = retrieveResponses(oldActions, actionId);
      Collection<String> modifiedResponse = retrieveResponsesWithModifiedSchema(newResponses, oldResponses, commonResponseKeySet);
     
      List<ActionDiff> allDifferences = new ArrayList<ActionDiff>();
      if(CollectionUtils.isNotEmpty(addedResponseKeySet)){
        allDifferences.add(new ResponseDiff(DiffType.NEW, commonAction, addedResponseKeySet));
      }
      
      if(CollectionUtils.isNotEmpty(deletedResponseKeySet)){
        allDifferences.add(new ResponseDiff(DiffType.DELETED, commonAction, deletedResponseKeySet));
      }
      
      if(CollectionUtils.isNotEmpty(modifiedResponse)){
        allDifferences.add(new ResponseDiff(DiffType.UPDATED, commonAction, modifiedResponse));        
      }
      
      return allDifferences.stream();
    }).collect(Collectors.toList());
    return actionsWithDifferentResponseSchemas;
  }

  /**
   * The method returns the Responses for a corresponding Action.
   * 
   * @param Map<ActionId, Action>
   * @param ActionId
   * @return Map<String, Response>
   */
  public Map<String, Response> retrieveResponses(Map<ActionId, Action> actionMap, ActionId actionId) {
    return actionMap.get(actionId).getResponses();
  }

  /**
   * The method determines the Existing Response objects whose MimetTypes has been created/deleted/modified.
   * MimeTypes Updates are determined on the Schema Attribute.
   * 
   * @param Map<String, Response>
   * @param Map<String, Response>
   * @param Collection<String>
   * @return Collection<String>
   */
  private Collection<String> retrieveResponsesWithModifiedSchema(Map<String, Response> newActionResponses, 
      Map<String, Response> oldActionResponses, Collection<String> commonResponses) {
    
    List<String> changedResponseSchema = commonResponses.stream().flatMap(responseKey -> {
      Response newResponse = newActionResponses.get(responseKey);
      Response oldResponse = oldActionResponses.get(responseKey);
      
      Map<String, MimeType> newResponseMimeTypes = retrieveMimeTypeDetails(newResponse);
      Map<String, MimeType> oldResponseMimeTypes = retrieveMimeTypeDetails(oldResponse);
      
      Collection<String> newlyAddedMimeTypes = fetchDifferenceInMimeTypes(newResponseMimeTypes, oldResponseMimeTypes);
      Collection<String> deletedMimeTypes = fetchDifferenceInMimeTypes(oldResponseMimeTypes, newResponseMimeTypes);
      
      List<String> allResponseDifferences = new ArrayList<String>();
      
      addMimeTypeDifferenceDetails(allResponseDifferences, responseKey, newlyAddedMimeTypes, IS_ADDED_TEXT);
      addMimeTypeDifferenceDetails(allResponseDifferences, responseKey, deletedMimeTypes, IS_DELETED_TEXT);

      
      Collection<String> existingMimeTypes = CollectionUtils.intersection(newResponseMimeTypes.keySet(), oldResponseMimeTypes.keySet());
      for(String mimeType : existingMimeTypes){
        MimeType newMimeType = newResponseMimeTypes.get(mimeType);
        MimeType oldMimeType = oldResponseMimeTypes.get(mimeType);
        if(!StringUtils.equals(newMimeType.getSchema(), oldMimeType.getSchema())){
          addChangedMimeType(allResponseDifferences, responseKey, IS_UPDATED_TEXT, mimeType);
        }
      }      
      
      return allResponseDifferences.stream();
    }).collect(Collectors.toList());
    
    return changedResponseSchema;
  }
  
  /**
   * The method adds MimeType that is changed(addition/deletion/updation) to the input List.
   * 
   * @param List<String>
   * @param String
   * @param Collection<String>
   * @param String
   */
  public void addMimeTypeDifferenceDetails(List<String> changedResponseSchema, 
      String responseKey, Collection<String> mimeTypesToAdd, String textToBeAdded) {
    for(String mimeType : mimeTypesToAdd){
      addChangedMimeType(changedResponseSchema, responseKey, textToBeAdded, mimeType);
    }
  }
  
  
  public boolean addChangedMimeType(List<String> changedResponseSchema, String responseKey,
      String textToBeAdded, String mimeType) {
    return changedResponseSchema.add(RESPONSE + responseKey + MIME_TYPE + mimeType + textToBeAdded);
  }

  /**
   * The method returns the list of MimeTypes for a response. If the mime - types is null, it returns a new HashMap.
   * 
   * @param Response
   * @return Map<String, MimeType>
   */
  private Map<String, MimeType> retrieveMimeTypeDetails(Response response) {
    Map<String, MimeType> mimeTypes = response.getBody();
    if(MapUtils.isEmpty(mimeTypes)){
      return new HashMap<String, MimeType>();
    }
    return mimeTypes;
  }

  /**
   * The method retrieves the difference in MimeTypes.
   * The difference is calculated as follows:
   * CASE 1: If OldResponseMimeTypes is null or empty && NewResponseMimeTypes is non-empty entity, then difference is equal to NewResponseMimeTypes.
   * CASE 2: If NewResponseMimeTypes is null or empty && OldResponseMimeTypes is non-empty entity, then difference is equal to OldResponseMimeTypes.
   * CASE 3: In the case of both MimeTypes being non-empty, the difference is determined using CollectionUtils.subtract function.
   * CASE 4: In any other scenarios, null would be returned.
   * 
   * @param Map<String, MimeType> newResponseMimeTypes
   * @param Map<String, MimeType> oldResponseMimeTypes
   * @return Collection<String>
   */
  public Collection<String> fetchDifferenceInMimeTypes(Map<String, MimeType> newResponseMimeTypes,
      Map<String, MimeType> oldResponseMimeTypes) {
    Collection<String> differenceMimeTypes = null;
    Set<String> oldResponseMimeTypeKeyset = oldResponseMimeTypes.keySet();
    Set<String> newResponseMimeTypeKeySet = newResponseMimeTypes.keySet();
    differenceMimeTypes = CollectionUtils.subtract(newResponseMimeTypeKeySet, oldResponseMimeTypeKeyset);       
    return differenceMimeTypes;
  }


  /**
   * The method returns all the responses associated with a corresponding ActionId
   * @param Map<ActionId, Action>
   * @param ActionId
   * @return Set<String>
   */
  public Set<String> retrieveResponseKeyset(Map<ActionId, Action> actionMap, ActionId actionId) {
    return retrieveResponses(actionMap, actionId).keySet();
  }
}
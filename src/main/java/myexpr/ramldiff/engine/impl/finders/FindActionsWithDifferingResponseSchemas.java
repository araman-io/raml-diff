package myexpr.ramldiff.engine.impl.finders;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;
import myexpr.ramldiff.diff.ActionId;
import myexpr.ramldiff.diff.DiffType;
import myexpr.ramldiff.diff.SchemaDiff;
import myexpr.ramldiff.engine.Finder;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.model.Action;
import org.raml.model.Response;

public class FindActionsWithDifferingResponseSchemas implements Finder {

  /**
   * a) find common actions b) find common status code c) find responses associated with status
   * codes d)for responses associated with the same status code collate schema into a set e) compare
   * set
   */
  @Override
  public List<ActionDiff> diff(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    Collection<ActionId> commonActionIds = this.getCommonActions(newActions, oldActions);
    List<ActionDiff> differences = null;

    //@formatter:off
    differences = 
      commonActionIds
        .stream()
        .map( actionId -> {
            Action action = newActions.get(actionId);
            Map<String, Response> newResponseMap = this.getResponseMapForAction(newActions, actionId);
            Map<String, Response> oldResponseMap = this.getResponseMapForAction(oldActions, actionId);
            
            Collection<String> commonStatusCodes = CollectionUtils.intersection(newResponseMap.keySet(), oldResponseMap.keySet());
            
            return new CommonStatusCodeContext(commonStatusCodes, newResponseMap, oldResponseMap, action, actionId);
        })
        .flatMap(context -> {
          List<SchemaDifferenceContext> allDifferences;
          allDifferences = 
            context.ids
              .stream()
              .map(statusCode -> {
                SchemaDifferenceContext schemaDifferenceContext = null;
                Set<String> schemaFromNewResponse = this.getCollatedSchemaForResponseWithStatusCode(statusCode, context.newResponseMap);
                Set<String> schemaFromOldResponse = this.getCollatedSchemaForResponseWithStatusCode(statusCode, context.oldResponseMap);
                Collection<String> schemaDifferences = CollectionUtils.subtract(schemaFromNewResponse, schemaFromOldResponse);
                
                if ( CollectionUtils.isNotEmpty(schemaDifferences) ) {
                    schemaDifferenceContext = new SchemaDifferenceContext(schemaDifferences, statusCode, context.action, context.actionId);
                } 
                return schemaDifferenceContext;
              })
              .filter( o -> isNotNull(o))
              .collect(Collectors.toList());
          
          return allDifferences.stream();
        })
        .map(schemaContext -> {
          return new SchemaDiff(DiffType.UPDATED, schemaContext.action, schemaContext.schemaDifferences, schemaContext.statusCode);
        })
        .collect(Collectors.toList());
      //@formatter:on


    return differences; // return method block
  }

  protected Set<String> getCollatedSchemaForResponseWithStatusCode(String statusCode, Map<String, Response> responseMap) {
    Response r = responseMap.get(statusCode);
    Set<String> collatedSchemas = new HashSet<String>();

    if (isNotNull(r.getBody()) && isNotNull(r.getBody().values())) {
      //@formatter:off
      collatedSchemas = 
          r.getBody().values()
            .stream()
            .filter(mimeType -> isNotNull(mimeType.getSchema()))
            .map(mimeType -> {return mimeType.getSchema();})
            .collect(Collectors.toSet());
      //@formatter:on
    }

    return collatedSchemas;
  }

  protected boolean isNotNull(Object o) {
    boolean result = (o != null) ? true : false;
    return result;
  }

  protected Map<String, Response> getResponseMapForAction(Map<ActionId, Action> actions, ActionId actionId) {
    return actions.get(actionId).getResponses();
  }

  protected Collection<ActionId> getCommonActions(Map<ActionId, Action> newActions, Map<ActionId, Action> oldActions) {
    Collection<ActionId> commonActionIds = null;
    commonActionIds = CollectionUtils.intersection(newActions.keySet(), oldActions.keySet());
    return commonActionIds;
  }

  public boolean equals(Object o) {
    boolean result = false;

    if (FindActionsWithDifferingResponseSchemas.class.getName().equals(o.getClass().getName())) {
      result = true;
    }

    return result;
  }

}


class CommonStatusCodeContext {

  Collection<String> ids;
  Map<String, Response> newResponseMap;
  Map<String, Response> oldResponseMap;
  Action action;
  ActionId actionId;

  public CommonStatusCodeContext(Collection<String> ids, Map<String, Response> newResponseMap,
      Map<String, Response> oldResponseMap, Action action, ActionId actionId) {
    super();
    this.ids = ids;
    this.newResponseMap = newResponseMap;
    this.oldResponseMap = oldResponseMap;
    this.action = action;
    this.actionId = actionId;
  }
}


class SchemaDifferenceContext {

  Collection<String> schemaDifferences;
  String statusCode;
  Action action;
  ActionId actionId;

  public SchemaDifferenceContext(Collection<String> schemaDifferences, String statusCode, Action action,
      ActionId actionId) {
    this.schemaDifferences = schemaDifferences;
    this.statusCode = statusCode;
    this.action = action;
    this.actionId = actionId;
  }
}

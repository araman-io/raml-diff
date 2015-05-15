package engine.impl.finders;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.model.Action;
import org.raml.model.Response;

import diff.ActionDiff;
import diff.ActionId;
import diff.DiffType;
import diff.SchemaDiff;
import engine.Finder;

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

    differences =
        commonActionIds
            .stream()
            .map(
                actionId -> {
                  Action action = newActions.get(actionId);
                  Map<String, Response> newResponseMap = this.getResponseMapForAction(newActions, actionId);
                  Map<String, Response> oldResponseMap = this.getResponseMapForAction(oldActions, actionId);

                  Collection<String> commonStatusCodes =
                      CollectionUtils.intersection(newResponseMap.keySet(), oldResponseMap.keySet());

                  return new FindSchemaDiffContext(commonStatusCodes, newResponseMap, oldResponseMap, action, actionId);
                })
            .flatMap(
                context -> {
                  List<CollateSchemaDiffContext> allSchemaDifferences =
                      context.ids
                          .stream()
                          .map(
                              statusCode -> {
                                Set<String> schemasFromNewResponse =
                                    this.getCollatedSchemaForResponseWithStatusCode(statusCode, context.newResponseMap);
                                Set<String> schemasFromOldResponse =
                                    this.getCollatedSchemaForResponseWithStatusCode(statusCode, context.oldResponseMap);
                                Collection<String> schemaDifferences =
                                    CollectionUtils.subtract(schemasFromNewResponse, schemasFromOldResponse);

                                return new CollateSchemaDiffContext(schemaDifferences, schemasFromNewResponse,
                                    schemasFromOldResponse, context.action, context.actionId);
                              }).collect(Collectors.toList());

                  return allSchemaDifferences.stream();
                })
            .map(schemaDiffContext -> {
              return new SchemaDiff(DiffType.UPDATED, schemaDiffContext.action, schemaDiffContext.schemaDifferences);
              })
            .collect(Collectors.toList());
    
    return differences; // return method block
  }

  protected Set<String> getCollatedSchemaForResponseWithStatusCode(String statusCode, Map<String, Response> responseMap) {
    Response r = responseMap.get(statusCode);
    Set<String> collatedSchemas = new HashSet<String>();

    if (isNotNull(r.getBody()) && isNotNull(r.getBody().values())) {
      collatedSchemas = r.getBody().values().stream().map(mimeType -> {
        return mimeType.getSchema();
      }).collect(Collectors.toSet());
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


class FindSchemaDiffContext {
  public FindSchemaDiffContext(Collection<String> ids, Map<String, Response> newResponseMap,
      Map<String, Response> oldResponseMap, Action action, ActionId actionId) {
    super();
    this.ids = ids;
    this.newResponseMap = newResponseMap;
    this.oldResponseMap = oldResponseMap;
    this.action = action;
    this.actionId = actionId;
  }

  Collection<String> ids;
  Map<String, Response> newResponseMap;
  Map<String, Response> oldResponseMap;
  Action action;
  ActionId actionId;
}


class CollateSchemaDiffContext {

  Collection<String> schemaDifferences;
  Set<String> schemaFromNewResponse;
  Set<String> schemaFromOldResponse;
  Action action;
  ActionId actionId;

  public CollateSchemaDiffContext(Collection<String> schemaDifferences, Set<String> schemaFromNewResponse,
      Set<String> schemasFromOldResponse, Action action, ActionId actionId) {
    this.schemaDifferences = schemaDifferences;
    this.schemaFromNewResponse = schemaFromNewResponse;
    this.schemaFromOldResponse = schemasFromOldResponse;
    this.action = action;
    this.actionId = actionId;
  }
}

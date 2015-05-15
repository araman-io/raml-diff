package engine.impl.finders;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.raml.model.Action;
import org.raml.model.ActionType;

import diff.ActionId;
import engine.MockHelper;

public class FindActionsWithUpdatedResponseSchemaTest extends MockHelper {
  
  Map<ActionId, Action> newActions = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActions = new HashMap<ActionId, Action>();
  FindActionsWithUpdatedResponseSchema classToTest = new FindActionsWithUpdatedResponseSchema();
  
  @Before
  public void init(){
    addMapEntryWithResponse(newActions, ActionType.GET, "/test", mockResponse());
    addMapEntryWithResponse(oldActions, ActionType.GET, "/test", mockResponse());
  }
  
  @Test
  public void shouldReturnOneUpdationOfResponseWhenMIMESchemaIsAddedToResponse(){
    addMapEntryWithResponse(newActions, ActionType.GET, "/test", mockResponseWithAddedMime());
    assertEquals(1, classToTest.diff(newActions, oldActions).size());
  }
  
  @Test
  public void shouldReturnOneUpdationOfResponseWhenMIMESchemaIsDeletedFromResponse(){
    addMapEntryWithResponse(newActions, ActionType.GET, "/test", mockResponseWithDeletedMime());
    assertEquals(1, classToTest.diff(newActions, oldActions).size());
  }
  
  @Test
  public void shouldReturnOneUpdationOfResponseWhenMIMESchemaIsUpdatedInResponse(){
    addMapEntryWithResponse(newActions, ActionType.GET, "/test", mockResponseWithModifiedMimeSchema());
    assertEquals(1, classToTest.diff(newActions, oldActions).size());
  }
}
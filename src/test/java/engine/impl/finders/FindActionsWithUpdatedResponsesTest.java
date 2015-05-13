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

public class FindActionsWithUpdatedResponsesTest extends MockHelper {
  
  Map<ActionId, Action> newActions = new HashMap<ActionId, Action>();
  Map<ActionId, Action> oldActions = new HashMap<ActionId, Action>();
  FindActionsWithUpdatedResponses classToTest = new FindActionsWithUpdatedResponses();
  
  @Before
  public void init(){
    addMapEntryWithResponse(newActions, ActionType.GET, "/test", mockResponse());
    addMapEntryWith(oldActions, ActionType.GET, "/test");
  }
  
  
  @Test
  public void shouldReturnOneAdditionOfResponseWhenAResponseIsAddedToNewRaml(){
    assertEquals(1, classToTest.diff(newActions, oldActions).size());
  }
  
  @Test
  public void shouldReturnOneDeletionOfResponseWhenAResponseIsDeletedFromNewRaml(){
    assertEquals(1, classToTest.diff(oldActions, newActions).size());
  }
  
  @Test
  public void shouldReturnNoChanegeInResponseWhenAResponseIsNeitherAddedNorRemoved(){
    assertEquals(0, classToTest.diff(oldActions, oldActions).size());
    assertEquals(0, classToTest.diff(newActions, newActions).size());
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
package beans;

import java.io.Serializable;
import java.util.List;

public class RamlDiffServiceResponse implements Serializable {

  private static final long serialVersionUID = -7743690864501781009L;
  
  private int responseCode;
  private List<String> ramlFileDifferences;
  private String errorMessage;
  
  // Getters & Setters
  public int getResponseCode(){
    return responseCode;
  }

  public List<String> getRamlFileDifferences() {
    return ramlFileDifferences;
  }

  public void setRamlFileDifferences(List<String> ramlFileDifferences) {
    this.ramlFileDifferences = ramlFileDifferences;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

}
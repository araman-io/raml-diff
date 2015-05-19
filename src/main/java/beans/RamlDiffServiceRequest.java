package beans;

import java.io.Serializable;

public class RamlDiffServiceRequest implements Serializable {

  private static final long serialVersionUID = 6938154565341684226L;
  
  private String oldRamlFileURL;
  private String newRamlFileURL;
  
  // Getters & Setters
  public String getOldRamlFileURL(){
    return oldRamlFileURL;
  }

  public String getNewRamlFileURL() {
    return newRamlFileURL;
  }

  public void setNewRamlFileURL(String newRamlFileURL) {
    this.newRamlFileURL = newRamlFileURL;
  }

  public void setOldRamlFileURL(String oldRamlFileURL) {
    this.oldRamlFileURL = oldRamlFileURL;
  }
}
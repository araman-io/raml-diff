package myexpr.ramldiff.diff;

import org.raml.model.Action;
import org.raml.model.ActionType;

public class ActionId {

  private ActionType type;
  private String resourceUri;

  public ActionId(Action action) {
    this(action.getType(), action.getResource().getUri());
  }

  public ActionId(ActionType type, String relativeUri) {
    this.type = type;
    this.resourceUri = relativeUri;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((resourceUri == null) ? 0 : resourceUri.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ActionId other = (ActionId) obj;
    if (resourceUri == null) {
      if (other.resourceUri != null)
        return false;
    } else if (!resourceUri.equals(other.resourceUri))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ActionId [type=" + type + ", resourceUri=" + resourceUri + "]";
  }

}

package diff;

import java.util.Collection;

import org.raml.model.Action;

public class SchemaDiff extends ActionDiff {

  Collection<String> schemaDifferences;
  Object statusCode;

  public SchemaDiff(DiffType diffType, Action action, Collection<String> schemaDifferences, String statusCode) {
    super(diffType, action);
    this.schemaDifferences = schemaDifferences;
    this.statusCode = statusCode;
  }

  @Override
  public String toString() {
    return "SchemaDiff [schemaDifferences=" + schemaDifferences + ", statusCode=" + statusCode + ", diffType="
        + diffType + ", action=" + action + "]";
  }

}

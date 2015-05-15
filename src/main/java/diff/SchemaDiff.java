package diff;

import java.util.Collection;

import org.raml.model.Action;

public class SchemaDiff extends ActionDiff {

  private Collection<String> schemaDifferences;

  public SchemaDiff(DiffType diffType, Action action, Collection<String> schemaDifferences) {
    super(diffType, action);
    this.schemaDifferences = schemaDifferences;
  }

}

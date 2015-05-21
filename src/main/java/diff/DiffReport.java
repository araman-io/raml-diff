package diff;

import java.util.List;

public class DiffReport {
  
  String left;
  String right;
  List<ActionDiff> differences;

  public DiffReport(String left, String right, List<ActionDiff> differences) {
    super();
    this.left = left;
    this.right = right;
    this.differences = differences;
  }

  public String getLeft() {
    return left;
  }

  public void setLeft(String left) {
    this.left = left;
  }

  public String getRight() {
    return right;
  }

  public void setRight(String right) {
    this.right = right;
  }

  public List<ActionDiff> getDifferences() {
    return differences;
  }

  public void setDifferences(List<ActionDiff> differences) {
    this.differences = differences;
  }

}

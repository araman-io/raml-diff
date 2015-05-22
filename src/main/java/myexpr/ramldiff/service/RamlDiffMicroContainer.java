package myexpr.ramldiff.service;

import static spark.Spark.get;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import myexpr.ramldiff.diff.ActionDiff;

import com.google.gson.Gson;

public class RamlDiffMicroContainer {
  
  public static final RamlDiffService SERVICE_INSTANCE = new RamlDiffService();

  public static void main(String[] args) {

    get("/", (request, response) -> {
      return "RAML diff service is available. \n" + "/findDiff?new=<<fileURL>>&old=<<fileURL>>";
    });

    get("/findDiff", (request, response) -> {

      List<ActionDiff> allDifferences = null;
      String newRamlFilePath = request.queryParams("new");
      String oldRamlFilePath = request.queryParams("old");

      URL oldFileUrl = new URL(oldRamlFilePath);
      URL newFileUrl = new URL(newRamlFilePath);
      allDifferences = SERVICE_INSTANCE .diff(newFileUrl.getFile(), oldFileUrl.getFile());

      Gson gson = new Gson();

      //@formatter:off
      String toJson = 
          allDifferences.stream()
          .map(d -> gson.toJson(d.getState()))
          .collect(Collectors.joining(",", "[", "]"));

      return toJson;
    });
    //@formatter:on
  }
}

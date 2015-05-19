package utility;

import spark.ResponseTransformer;
import beans.RamlDiffServiceRequest;
import beans.RamlDiffServiceResponse;

import com.google.gson.Gson;

public class JsonUtility {
  
  public static String toJson(Object object){
    return new Gson().toJson(object);
  }
  
  public static RamlDiffServiceRequest toRequestObject(String jsonString){
    return new Gson().fromJson(jsonString, RamlDiffServiceRequest.class);
  }
  
  public static RamlDiffServiceResponse toResponseObject(String jsonString){
    return new Gson().fromJson(jsonString, RamlDiffServiceResponse.class);
  }
  
  public static ResponseTransformer json(){
    return JsonUtility::toJson;
  }

}

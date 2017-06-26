package com.katalyst.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class SkuHttpClient {
	
static Logger logger = Logger.getLogger(HttpClient.class);
	
	
	
	public static JSONObject sendto(JSONObject requestEncodeddata, String method, String param) throws Exception {

        String responseJsonString = "";

        String respondMessage = null;

        String errorMessage = null;
        
        JSONObject postData = null;
  
        HttpURLConnection conn = null;

        JSONObject responseJSON = null;

        OutputStream os = null;

        String baseurl = "https://app.skuvault.com/api";

        String errorCodeHeader = null;

       // logger.debug("sending request: " + baseurl + " method: " + method + " param: " + param);

        try {

            URL url;

            if (method.equalsIgnoreCase("GET")){
            	//System.out.println(baseurl.toString() + "/" + param);

                url = new URL(baseurl+"/" +param);  
              //  logger.debug("URL to which data is posted: "+url);

            }else{

                if(method.equalsIgnoreCase("POST") && param != null)

                {

                url = new URL(baseurl+"/"+param);

                }

                else

                {

                url= new URL(baseurl.toString());   

                }
                           }
            //logger.debug("URL to which data is posted: "+url);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);

            conn.setRequestMethod(method);
            if(method.equals("POST"))
            {
            	conn.setRequestProperty("Accept", "application/json");
            	conn.setRequestProperty("Content-Type", "application/json");
            }

            if (requestEncodeddata != null) {

                os = conn.getOutputStream();
                
                postData = requestEncodeddata;
                
                os.write(postData.toString().getBytes());

                os.flush();

            }


            int returnCode = conn.getResponseCode();

            respondMessage = conn.getResponseMessage();

            errorCodeHeader = "Error code: " + returnCode + ": " + respondMessage; 

          //  logger.info("return code: " + returnCode + " response message: " + respondMessage);

            if (returnCode >= 400) {

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				
				String output;
				
				while ((output = br.readLine()) != null) {
				
				responseJsonString = responseJsonString + output;
				
				}
				
				responseJSON = getJSONDocument(responseJsonString);
				responseJSON.put("returnCode", returnCode);
				
				JSONObject errorJson = (JSONObject) responseJSON.get("error");
				
				if (errorJson != null) {
				
				errorMessage = responseJSON.get("status") +", "+ errorJson.get("message").toString()+","+errorJson.get("correctiveAction").toString();
				
				}
				
				return responseJSON;
				
				}
				
				
				            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				
				
				            String output;
				
				            while ((output = br.readLine()) != null) {
				
				                responseJsonString = responseJsonString + output;
				
				            }
				
				            if ((responseJsonString != null) && (responseJsonString.length() > 0)) {
				
				               // logger.debug("response message: " + respondMessage);
				
				              //  logger.debug("response from host: " + responseJsonString);
				                
				              //  logger.debug("before this what happens");
				
				                responseJSON = getJSONDocument(responseJsonString);
				                
				              //  logger.debug("after this what happens");
				
				                responseJSON.put("returnCode", returnCode);
				              //  logger.debug("ResponseJSON after Converting:" + responseJSON.toString() );
				
				            }
				
				
				        } catch (Exception e) {
				
				        logger.error("General Exception:" + e.getLocalizedMessage() + e);
				
				        } finally {
				
				            if (os != null) {
				
				                try {
				
				                    os.close();
				
				                } catch (IOException e) {
				
				                    // ignore
				
				                }
				
				            }
				
				            if (conn != null) {
				
				                conn.disconnect();
				
				            }
				
				        }
        		
				
        			//logger.debug("Response JSON"+ responseJSON.toString());
				        return responseJSON;
				
				    }
	
	private static JSONObject getJSONDocument(String jsonString){
		try {
			JSONObject theobj = (JSONObject)JSONSerializer.toJSON(jsonString);
			return theobj;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}


}

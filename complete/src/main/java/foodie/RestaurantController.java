package foodie;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class RestaurantController {
	
	
    @RequestMapping("/restaurant")
    public ResponseEntity<List<Restaurant>> location(@RequestParam(value="address", required = true) String address) {
    	if(address == null || address.isEmpty()) {
    		List<Restaurant> temp = new ArrayList<Restaurant>();
    		String eAddress = "YOU MUST ENTER AN ADDRESS";
    		String name = "YOU MUST ENTER AN ADDRESS";
    		String cuisines = "YOU MUST ENTER AN ADDRESS";
    		double rating = 0.0;
    		Restaurant error = new Restaurant(name, eAddress, rating, cuisines);
    		temp.add(error);
    		return new ResponseEntity<List<Restaurant>>(temp, HttpStatus.BAD_REQUEST);
    	}
    	final String geoURI = "https://api.geocod.io/v1.3/geocode?q="+address+"&api_key=your_api_key_here";
    	System.out.println("Address is: "+address);
    	RestTemplate rt = new RestTemplate();
    	ResponseEntity<String> result = rt.exchange(geoURI, HttpMethod.GET, null, String.class);
    	HttpStatus statusCode = result.getStatusCode();
    	if(statusCode!= HttpStatus.OK) {
    		List<Restaurant> temp = new ArrayList<Restaurant>();
    		String eAddress = "INTERNAL SERVER ERROR.  PLEASE TRY AGAIN LATER.";
    		String name = "INTERNAL SERVER ERROR.  PLEASE TRY AGAIN LATER.";
    		String cuisines = "INTERNAL SERVER ERROR.  PLEASE TRY AGAIN LATER.";
    		double rating = 0.0;
    		Restaurant error = new Restaurant(name, eAddress, rating, cuisines);
    		temp.add(error);
    		return new ResponseEntity<List<Restaurant>>(temp, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	ObjectMapper mapper = new ObjectMapper();
    	double lat = 0.0;
    	double lng = 0.0;
    	try {
    		JsonNode rootNode = mapper.readValue(result.getBody(), JsonNode.class);
    		lat = rootNode.get("results")
    				.get(0)
    				.get("location")
    				.get("lat")
    				.asDouble();
    		lng = rootNode.get("results")
    				.get(0)
    				.get("location")
    				.get("lng")
    				.asDouble();
    		System.out.println("latitude = "+lat);
    		System.out.println("longitude = "+lng);
    	}
    	catch(IOException ie) {
    		ie.printStackTrace();
    	}
    	
    	final String zomatoURI = "https://developers.zomato.com/api/v2.1/geocode?lat="+lat+"&lon="+lng;
    	RestTemplate rs = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.add("user-key", "your_api_key_here");
    	HttpEntity<String> entity = new HttpEntity<String>(headers);
    	ResponseEntity<String> zomatoResult = rs.exchange(zomatoURI, HttpMethod.GET, entity, String.class);
    	HttpStatus statusCodeZomato = result.getStatusCode();
    	if(statusCodeZomato!= HttpStatus.OK) {
    		List<Restaurant> temp = new ArrayList<Restaurant>();
    		String eAddress = "INTERNAL SERVER ERROR.  PLEASE TRY AGAIN LATER.";
    		String name = "INTERNAL SERVER ERROR.  PLEASE TRY AGAIN LATER.";
    		String cuisines = "INTERNAL SERVER ERROR.  PLEASE TRY AGAIN LATER.";
    		double rating = 0.0;
    		Restaurant error = new Restaurant(name, eAddress, rating, cuisines);
    		temp.add(error);
    		return new ResponseEntity<List<Restaurant>>(temp, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	String body = zomatoResult.getBody();
    	List<Restaurant> rList = new ArrayList<Restaurant>();
    	
    	try {
    		JsonNode rootNode = mapper.readValue(body, JsonNode.class);
    		JsonNode nearby_restaurants = rootNode.get("nearby_restaurants");
    		Iterator<JsonNode> iter = nearby_restaurants.iterator();
    		while(iter.hasNext()) {
    			JsonNode temp = iter.next();
    			String name = temp.get("restaurant")
    					.get("name")
    					.asText();
    			String restAddress = temp.get("restaurant")
    					.get("location")
    					.get("address")
    					.asText();
    			double rating = temp.get("restaurant")
    					.get("user_rating")
    					.get("aggregate_rating")
    					.asDouble();
    			String cuisines = temp.get("restaurant")
    					.get("cuisines")
    					.asText();
    			Restaurant newRestaurant = new Restaurant(name, restAddress, rating, cuisines);
    			rList.add(newRestaurant);
    		}
    	}
    	catch(IOException ie) {
    		ie.printStackTrace();
    	}
    	return new ResponseEntity<List<Restaurant>>(rList, HttpStatus.OK);
    }
    
}

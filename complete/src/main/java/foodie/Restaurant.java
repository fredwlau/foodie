package foodie;

public class Restaurant {
	
	private final String name;
    private final String address;
    private final double rating;
    private final String cuisines;
    
    public Restaurant(String name, String address, double rating, String cuisines) {
    	this.name = name;
    	this.address = address;
    	this.rating = rating;
    	this.cuisines = cuisines;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getAddress() {
    	return address;
    }
    
    public double getRating() {
    	return rating;
    }
    
    public String getCuisines() {
    	return cuisines;
    }
}
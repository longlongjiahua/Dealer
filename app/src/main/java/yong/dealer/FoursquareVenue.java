package yong.dealer;

public  class FoursquareVenue implements Comparable<FoursquareVenue>{
    private String name;
    private String city;
    private int distance;
    private String address;

    private String category;



    public FoursquareVenue() {
        this.name = "";
        this.city = "";

    }
    public int compareTo(FoursquareVenue other){

        return Integer.compare(distance, other.distance);
    }

    public void setAddress(String address){
        this.address= address;
    }
    public String getAddress(){
        return address;
    }

    public String getCity() {
        if (city.length() > 0) {
            return city;
        }
        return city;
    }

    public void setCity(String city) {
        if (city != null) {
            this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");
            ;
        }
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}


package uns.ac.rs.dto.request;

import jakarta.ws.rs.FormParam;

import java.io.InputStream;

public class AccommodationForm {
    @FormParam("file")
    public InputStream file;

    @FormParam("fileName")
    public String fileName;

    @FormParam("name")
    public String name;

    @FormParam("location")
    public String location;

    @FormParam("minGuests")
    public int minimumNoGuests;

    @FormParam("maxGuests")
    public int maximumNoGuests;

    @FormParam("tags")
    public String features;
}

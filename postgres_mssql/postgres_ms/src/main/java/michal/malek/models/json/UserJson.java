package michal.malek.models.json;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import michal.malek.CsvToListDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserJson {
    public String user_id;
    public String name;
    public String yelping_since;
    @JsonProperty("friends")
    @JsonDeserialize(using = CsvToListDeserializer.class)
    public List<String> ids;


}
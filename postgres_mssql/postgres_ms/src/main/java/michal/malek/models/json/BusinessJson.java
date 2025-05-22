package michal.malek.models.json;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessJson {
    public String business_id;
    public String name;
    public String address;
    public String city;
    public String state;
    public String postal_code;
    public double latitude;
    public double longitude;
    public int stars;
    public int review_count;
    public int is_open;
    public Map<String, String> attributes;

    @JsonProperty("categories")
    private String categoriesRaw;

    @JsonIgnore
    public List<String> categories = Collections.emptyList();

    public Map<String, String> hours;

    @JsonProperty("categories")
    public void setCategoriesRaw(String categoriesRaw) {
        this.categoriesRaw = categoriesRaw;
        if (categoriesRaw != null && !categoriesRaw.isEmpty()) {
            this.categories = Arrays.stream(categoriesRaw.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
    }
}

package michal.malek.models.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewJson {
    public String review_id;
    public String user_id;
    public String business_id;
    public int stars;
    public String date;
    public String text;
    public int useful;
    public int funny;
    public int cool;
}


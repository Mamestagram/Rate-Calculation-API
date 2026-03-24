package net.mamesosu.api.calculate.object;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rate {

    @JsonProperty("overall")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double overAll;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double stream;
    @JsonProperty("jumpstream")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double jumpStream;
    @JsonProperty("handstream")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double handStream;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double stamina;
    @JsonProperty("jackspeed")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double jackSpeed;
    @JsonProperty("chordjack")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double chordJack;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double technical;
}

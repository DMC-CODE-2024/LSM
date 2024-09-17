package com.eirs.lsm.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Token {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("api_domain")
    private String apiDomain;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Integer expiresIn;

}

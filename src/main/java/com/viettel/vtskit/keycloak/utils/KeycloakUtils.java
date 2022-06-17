package com.viettel.vtskit.keycloak.utils;

import org.json.JSONObject;

import java.util.Base64;

public class KeycloakUtils {
    private KeycloakUtils(){}

    public static JSONObject parseJwtToken(String token){
        String[] tokenWithoutBearer = token.split(" ");
        String[] chunks = tokenWithoutBearer[1].split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject jsonObject=new JSONObject(payload);
        return jsonObject;
    }

}

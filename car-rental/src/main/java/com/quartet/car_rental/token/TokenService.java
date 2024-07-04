package com.quartet.car_rental.token;

import java.util.Map;

public interface TokenService {
    Map<String, String> generateToken(String grantType, String email, String password, String refreshToken) throws Exception;
}

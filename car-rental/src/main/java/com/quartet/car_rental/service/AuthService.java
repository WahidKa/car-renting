package com.quartet.car_rental.service;

import com.quartet.car_rental.dto.request.AuthRequest;
import com.quartet.car_rental.dto.response.AuthResponse;

import java.util.Map;

public interface AuthService {
    AuthResponse register(AuthRequest request);
    Map<String, String> login(AuthRequest request);
    Map<String, String> generateToken(String grantType, String username, String password, String refreshToken) throws Exception;
}

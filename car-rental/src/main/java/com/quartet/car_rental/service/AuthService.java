package com.quartet.car_rental.service;

import com.quartet.car_rental.dto.request.AuthRequest;
import com.quartet.car_rental.dto.request.RegistrationRequest;
import com.quartet.car_rental.dto.response.AuthResponse;

import java.util.Map;

public interface AuthService {
    AuthResponse register(RegistrationRequest request);
    Map<String, String> login(AuthRequest request);
    Map<String, String> generateToken(String grantType, String username, String password, String refreshToken) throws Exception;
}

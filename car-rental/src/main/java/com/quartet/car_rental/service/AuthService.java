package com.quartet.car_rental.service;

import com.quartet.car_rental.dto.request.AuthRequest;
import com.quartet.car_rental.dto.request.RegistrationRequest;
import com.quartet.car_rental.dto.request.UpdateRoleRequest;
import com.quartet.car_rental.dto.response.AuthResponse;
import com.quartet.car_rental.dto.response.LoginResponse;
import com.quartet.car_rental.dto.response.UpdateRoleResponse;

import java.util.Map;

public interface AuthService {
    AuthResponse register(RegistrationRequest request);
    LoginResponse login(AuthRequest request);
    Map<String, String> generateToken(String grantType, String username, String password, String refreshToken) throws Exception;
    UpdateRoleResponse updateRole(UpdateRoleRequest request, String token);
}

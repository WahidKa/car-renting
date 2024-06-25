package com.quartet.car_rental;

import com.quartet.car_rental.config.RsaKeysConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties(RsaKeysConfig.class)
public class CarRentalApplication {
	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);

		outPutCAR();
		System.out.println("----------------------------------------");
		System.out.println("----- Car Rental App");
		System.out.println("----- Car Rental App => version : 0.0.1 - ");
		System.out.println("----- Car Rental App => port : 9800 ");
		System.out.println("----------------------------------------");
	}

	public static void outPutCAR() {
		System.out.println("=====================================================");
		System.out.println("                                                   ");
		System.out.println("		CCCCC    AAAAA    RRRRRR              ");
		System.out.println("		C        A   A    R    R              ");
		System.out.println("		C        AAAAA    RRRRRR              ");
		System.out.println("		C        A   A    R   R               ");
		System.out.println("		CCCCC    A   A    R    R              ");
		System.out.println("                                                   ");
		System.out.println("=====================================================");
	}

}

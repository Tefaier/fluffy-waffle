package com.example.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuctionSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionSiteApplication.class, args);
	}

}

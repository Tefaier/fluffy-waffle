package com.example.auction;

import com.example.auction.models.DBSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootTest
@EnableScheduling
class AuctionSiteApplicationTests extends DBSuite {

	@Test
	void contextLoads() {
	}

}

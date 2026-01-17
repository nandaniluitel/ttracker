package com.example.ttracker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TtrackerApplicationSmokeTest {
    void contextLoads() {
        //If the spring context starts, this test passes.
    }

}

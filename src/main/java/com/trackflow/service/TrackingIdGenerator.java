package com.trackflow.service;

import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TrackingIdGenerator {

    public String generateTrackingId() {
        int year = Year.now().getValue();
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.format("SHP-%d-%06d", year, randomNum);
    }
}
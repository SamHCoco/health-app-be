package com.samhcoco.healthapp.health;

import java.time.OffsetDateTime;

public class LocalTest {

    public static void main(String[] args) {
        System.out.println(OffsetDateTime.MIN.plusDays(1));
        System.out.println(OffsetDateTime.MAX.minusDays(1));
    }

}

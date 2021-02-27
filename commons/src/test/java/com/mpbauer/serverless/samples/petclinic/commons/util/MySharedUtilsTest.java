package com.mpbauer.serverless.samples.petclinic.commons.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MySharedUtilsTest {

    @Test
    void testGreetings(){
        String answer = MySharedUtils.greetings("mpbauer");
        Assertions.assertEquals("hello mpbauer", answer);
    }
}

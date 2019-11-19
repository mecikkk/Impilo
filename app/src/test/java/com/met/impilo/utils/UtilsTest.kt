package com.met.impilo.utils

import org.junit.Test

import org.junit.Assert.*

class UtilsTest {


    @Test
    fun cutWhitespaces() {
        assertEquals("emailJakis@com.pl", Utils.cutWhitespaces("email Jakis@com. pl"))
    }
}
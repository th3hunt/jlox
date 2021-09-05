package com.craftinginterpreters.lox;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class AppTest {
  @Test
  void testSanity() {
    App classUnderTest = new App();
    assertNotNull(classUnderTest, "app should exist!");
  }
}

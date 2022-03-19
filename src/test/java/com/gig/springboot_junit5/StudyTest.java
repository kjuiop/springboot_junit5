package com.gig.springboot_junit5;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : JAKE
 * @date : 2022/03/19
 */
class StudyTest {

    @Test
    void create() {
        Study study = new Study();
        assertNotNull(study);
    }

    @Test
    void create1() {
        Study study = new Study();
        assertNotNull(study);
    }


    @BeforeAll
    static void beforeAll() {
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("after all");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before each");
    }

    @AfterEach
    void afterEach() {
        System.out.println("after each");
    }


}
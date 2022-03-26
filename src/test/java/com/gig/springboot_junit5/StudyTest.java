package com.gig.springboot_junit5;

import com.gig.springboot_junit5.entity.Study;
import com.gig.springboot_junit5.entity.type.StudyStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

/**
 * @author : JAKE
 * @date : 2022/03/19
 */
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

    @Test
    @DisplayName("조건에 따라 실행되는 테스트")
    @EnabledOnOs({OS.MAC, OS.LINUX})
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11})
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "local")
    void test_by_condition() {
        String testEnv = System.getenv("TEST_ENV");

        // assumeTrue 를 사용하면, 해당 조건에 부합되지 않으면 밑의 소스는 실행 불가
        // assumeTrue("LOCAL".equalsIgnoreCase(testEnv));

        assumingThat("LOCAL".equalsIgnoreCase(testEnv), () ->  {
            Study actual = new Study(100);
            assertThat(actual.getLimit()).isGreaterThan(0);
        });

        assumingThat("DEV".equalsIgnoreCase(testEnv), () ->  {
            Study actual = new Study(10);
            assertThat(actual.getLimit()).isGreaterThan(0);
        });
    }

    @Test
    @DisplayName("조건에 따라 실행되지않는 테스트")
    @DisabledOnOs({OS.MAC, OS.LINUX})
    void test_by_condition_disabled() {
        String testEnv = System.getenv("TEST_ENV");

        assumingThat("LOCAL".equalsIgnoreCase(testEnv), () ->  {
            Study actual = new Study(100);
            assertThat(actual.getLimit()).isGreaterThan(0);
        });

        assumingThat("DEV".equalsIgnoreCase(testEnv), () ->  {
            Study actual = new Study(10);
            assertThat(actual.getLimit()).isGreaterThan(0);
        });
    }

    @Test
    @DisplayName("스터디 만들기")
    void create_new_study() {
        Study study = new Study(StudyStatus.ENDED, -10);
        assertNotNull(study);
        // 기대하는 값, 실제 값, 테스트 오류 메시지
        // assertEquals(StudyStatus.ENDED, study.getStudyStatus(), "스터디를 처음 만들면 상태값이 DRAFT이어야 한다.");

        /*
        assertEquals(StudyStatus.ENDED, study.getStudyStatus(), new Supplier<String>() {
            @Override
            public String get() {
                return "스터디를 처음 만들면 상태값이 DRAFT이어야 한다.";
            }
        });
         */

        // 람다식을 사용하면 테스트가 실행됐을 때만 문자열을 계산하기 때문에 성능적으로 유리할 수 있다.
        // assertAll 은 테스트 안의 모든 assert 문을 한번에 다 실행됨
        // 원래는 위에서 테스트 실패하면 테스트가 종료됨
        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStudyStatus(), () -> "스터디를 처음 만들면 상태값이 " + StudyStatus.DRAFT + " 이어야 한다."),
                () -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.")
        );

    }

    @Test
    @Disabled
    void create1_study_again() {
        Study study = new Study();
        assertNotNull(study);
    }

    @Test
    @DisplayName("스터디 오류 테스트")
    void create1_study_exception() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        String message = exception.getMessage();
        assertEquals("limit는 0보다 커야 한다.", exception.getMessage());
    }

    @Test
    @DisplayName("스터디 타임아웃 테스트")
    void create1_study_timeout() {
        // 단점
        // 실제 쓰레드의 시간을 모두 기다린 후 걸린 시간과 비교해서 테스트 결과를 내기 때문에
        // 쓰레드가 종료될 때까지 기다리게 된다.
        /*
        assertTimeout(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });
         */

        // 이 테스트 구문은 시간이 초과되면 바로 종료된다.
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });

        // Thread Local 을 사용하는 소스가 있으면 예상치 못한 결과가 나올 수 있다.
        // Spring Transaction 은 Thread Local 을 기본전략을 사용하는데,
        // Thread Local 은 다른 Thread 에서 공유가 되지 않는다.
        // 테스트에서 Spring Transaction 테스트가 잘 안될 수 있다.
        // 예를 들어 rollback 이 안되고 DB에 반영될 수 있다.
        // 따라서 assertTimeout 이 더 안정적이다.
    }

    @Test
    @DisplayName("스터디 만들기 테스트 assertThat")
    void create_study_assertThat() {
        Study actual = new Study(10);
        assertThat(actual.getLimit()).isGreaterThan(0);
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
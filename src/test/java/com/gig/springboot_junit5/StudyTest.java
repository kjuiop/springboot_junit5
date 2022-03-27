package com.gig.springboot_junit5;

import com.gig.springboot_junit5.entity.Study;
import com.gig.springboot_junit5.entity.type.StudyStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.*;
import org.mockito.internal.matchers.Find;

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
// properties 에서 선언 가능
// @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)

// 클래스마다 하나의 인스턴스를 공유한다.
// beforeAll 이 static 으로 선언되지 않아도 사용할 수 있다.
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)

// 확장테스트 클래스를 쓸 때 클래스 단위로 선언하여 사용한다.
// @ExtendWith(FindSlowTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest {

    // 각 클래스 단위별로 파라미터를 다르게 넣어줄 수 있다.
    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000L);

    @Order(3)
    @DisplayName("반복 테스트")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}, {totalRepetitions}")
    void repeatTest(RepetitionInfo repetitionInfo) {
        System.out.println("test " + repetitionInfo.getCurrentRepetition() + "/" +
                repetitionInfo.getTotalRepetitions());
    }

    @Order(2)
    @DisplayName("파라미터 반복 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요"})
    @EmptySource
    @NullSource
    @NullAndEmptySource
    void parameterizedTest(String message) {
        System.out.println(message);
    }

    @Order(0)
    @DisplayName("파라미터 반복 테스트 Csv")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(ints = {10, 20, 40})
    void parameterizedTestConvertWith(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter {

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @Order(1)
    @DisplayName("파라미터 반복 테스트 Csv")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTestCsvSource(Integer limit, String name) {
        Study study = new Study(limit, name);
        System.out.println(study);
    }

    @DisplayName("파라미터 반복 테스트 ArgumentAccessor")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTestArgumentAccessor(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        System.out.println(study);
    }

    @DisplayName("파라미터 반복 테스트 AggregatorWith")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTestAggregatorWith(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }

    static class StudyAggregator implements ArgumentsAggregator {

        // static inner class 나 public class 로 만들어야 사용할 수 있다.

        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return new Study(accessor.getInteger(0), accessor.getString(1));
        }
    }

    @Test
    @DisplayName("조건에 따라 실행되는 테스트")
    @EnabledOnOs({OS.MAC, OS.LINUX})
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11})
//    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "local")
    @Disabled
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
    @Disabled
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

    @DisplayName("스터디 만들기")
    @StudyCrudTest
    void create_new_study() {
        Study study = new Study(StudyStatus.DRAFT, 10);
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

    @DisplayName("스터디 오류 테스트")
    @StudyCrudTest
    void create1_study_exception() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        String message = exception.getMessage();
        assertEquals("limit는 0보다 커야 한다.", exception.getMessage());
    }

    @DisplayName("스터디 타임아웃 테스트")
    @TimeOutTest
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
            Thread.sleep(10);
        });

        // Thread Local 을 사용하는 소스가 있으면 예상치 못한 결과가 나올 수 있다.
        // Spring Transaction 은 Thread Local 을 기본전략을 사용하는데,
        // Thread Local 은 다른 Thread 에서 공유가 되지 않는다.
        // 테스트에서 Spring Transaction 테스트가 잘 안될 수 있다.
        // 예를 들어 rollback 이 안되고 DB에 반영될 수 있다.
        // 따라서 assertTimeout 이 더 안정적이다.
    }

    @DisplayName("스터디 만들기 테스트 assertThat")
    @StudyCrudTest
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
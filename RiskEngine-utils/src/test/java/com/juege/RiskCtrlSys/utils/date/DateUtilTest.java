package com.juege.RiskCtrlSys.utils.date;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilTest {

    @Test
    void testConvertLocalDateTime2Str() {
        assertThat(DateUtil.convertLocalDateTime2Str(LocalDateTime.of(2020, 1, 1, 0, 0, 0))).isEqualTo("result");
    }

    @Test
    void testConvertStr2LocalDateTime() {
        assertThat(DateUtil.convertStr2LocalDateTime("str")).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
    }

    @Test
    void testConvertTimestamp2LocalDateTime() {
        assertThat(DateUtil.convertTimestamp2LocalDateTime(0L)).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
    }

    @Test
    void testConvertLocalDateTime2Timestamp() {
        assertThat(DateUtil.convertLocalDateTime2Timestamp(LocalDateTime.of(2020, 1, 1, 0, 0, 0))).isEqualTo(0L);
    }

    @Test
    void testLocalDateTimePlusDays() {
        assertThat(DateUtil.localDateTimePlusDays(LocalDateTime.of(2020, 1, 1, 0, 0, 0), "days"))
                .isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
    }

    @Test
    void testLocalDateTimePlusSec() {
        assertThat(DateUtil.localDateTimePlusSec(LocalDateTime.of(2020, 1, 1, 0, 0, 0), "second"))
                .isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
    }
}

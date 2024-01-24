package config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class StoreDataTest {

    @Test
    void testOpenCSV() {
        // given
        StoreData sut = StoreData.fromPath(getFilePath("test-data.csv"));

        // when
        Map<String, Object> result = sut.load();

        // then
        assertThat(result)
                .isNotNull()
                .hasSize(8);
    }

    private static String getFilePath(String fileName) {
        return ClassLoader.getSystemClassLoader().getResource(fileName).getFile();
    }

}
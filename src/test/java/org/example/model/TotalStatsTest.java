package org.example.model;

import org.example.util.TypeDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.engine.DataFilterEngine;

import static org.junit.jupiter.api.Assertions.*;

class TotalStatsTest {

    private TotalStats totalStats;
    private DataFilterEngine engine;
    private TypeDetector typeDetector;

    @BeforeEach
    void setUp() {
        totalStats = new TotalStats();
        engine = new DataFilterEngine();
        typeDetector = new TypeDetector();
    }

    @Test
    void updateStat_handlesStringValues() {
        // Arrange
        String[] values = {"hello", "world", "a"};

        // Act
        for (String value : values) {
            DataType type = typeDetector.detectType(value);
            totalStats.updateStat(type, value);
        }

        // Assert
        StringStatistic stats = totalStats.getStringStatistic();
        assertEquals(3, stats.getTypeCnt());
        assertEquals(1, stats.getMinLen());
        assertEquals(5, stats.getMaxLen());
    }

    @Test
    void updateStat_handlesIntegerValues() {
        // Arrange
        String[] values = {"10", "-5", "0"};

        // Act
        for (String value : values) {
            DataType type = typeDetector.detectType(value);
            totalStats.updateStat(type, value);
        }

        // Assert
        IntegerStatistic stats = totalStats.getIntegerStatistic();
        assertEquals(3, stats.getTypeCnt());
        assertEquals(-5, stats.getMin());
        assertEquals(10, stats.getMax());
        assertEquals(5, stats.getSum()); // 10 + (-5) + 0
        assertEquals(5 / 3.0, stats.getMiddle());
    }

    @Test
    void updateStat_handlesFloatValues() {
        // Arrange
        String[] values = {"3.14", "-0.5", "1.0"};

        // Act
        for (String value : values) {
            DataType type = typeDetector.detectType(value);
            totalStats.updateStat(type, value);
        }

        // Assert
        FloatStatistic stats = totalStats.getFloatStatistic();
        assertEquals(3, stats.getTypeCnt());
        assertEquals(-0.5, stats.getMin(), 0.001);
        assertEquals(3.14, stats.getMax(), 0.001);
        assertEquals(3.64, stats.getSum(), 0.001); // 3.14 + (-0.5) + 1.0
        assertEquals(3.64 / 3, stats.getMiddle(), 0.001);
    }

    @Test
    void updateStat_handlesMixedTypes() {
        // Arrange
        String[] values = {"text", "42", "3.14", "", "-10"};

        // Act
        for (String value : values) {
            DataType type = typeDetector.detectType(value);
            totalStats.updateStat(type, value);
        }

        // Assert
        assertEquals(2, totalStats.getStringStatistic().getTypeCnt()); // "text" and ""
        assertEquals(2, totalStats.getIntegerStatistic().getTypeCnt()); // "42" and "-10"
        assertEquals(1, totalStats.getFloatStatistic().getTypeCnt()); // "3.14"
    }

    @Test
    void updateStat_handlesEmptyString() {
        // Act
        DataType type = typeDetector.detectType("");
        totalStats.updateStat(type, "");

        // Assert
        StringStatistic stats = totalStats.getStringStatistic();
        assertEquals(1, stats.getTypeCnt());
        assertEquals(0, stats.getMinLen());
        assertEquals(0, stats.getMaxLen());
    }

    @Test
    void updateStat_handlesLargeNumbers() {
        // Arrange
        String bigInt = "2147483648"; // > Integer.MAX_VALUE
        String bigFloat = "1.7976931348623157E308"; // ~Double.MAX_VALUE

        // Act & Assert
        assertDoesNotThrow(() -> {
            DataType typeInt = typeDetector.detectType(bigInt);
            totalStats.updateStat(typeInt, bigInt);
        });
        assertDoesNotThrow(() -> {
            DataType typeFloat = typeDetector.detectType(bigFloat);
            totalStats.updateStat(typeFloat, bigFloat);
        });

        // Big integer should be treated as INTEGER (fits in Long) and big float as FLOAT
        assertEquals(1, totalStats.getIntegerStatistic().getTypeCnt());
        assertEquals(1, totalStats.getFloatStatistic().getTypeCnt());
    }

    @Test
    void updateStat_throwsForInvalidNumbers() {
        // Arrange
        String invalidNumber = "123ABC";

        // Act
        DataType type = typeDetector.detectType(invalidNumber);
        totalStats.updateStat(type, invalidNumber);

        // Assert
        assertEquals(DataType.STRING, type);
        assertEquals(1, totalStats.getStringStatistic().getTypeCnt());
    }

    @Test
    void getType_detectsStringType() {
        assertEquals(DataType.STRING, typeDetector.detectType("hello"));
        assertEquals(DataType.STRING, typeDetector.detectType("123ABC"));
        assertEquals(DataType.STRING, typeDetector.detectType(""));
    }

    @Test
    void getType_detectsIntegerType() {
        assertEquals(DataType.INTEGER, typeDetector.detectType("42"));
        assertEquals(DataType.INTEGER, typeDetector.detectType("-100"));
        assertEquals(DataType.INTEGER, typeDetector.detectType("0"));
    }

    @Test
    void getType_detectsFloatType() {
        assertEquals(DataType.FLOAT, typeDetector.detectType("3.14"));
        assertEquals(DataType.FLOAT, typeDetector.detectType("-0.5"));
        assertEquals(DataType.FLOAT, typeDetector.detectType("1.0"));
        assertEquals(DataType.FLOAT, typeDetector.detectType("1.7976931348623157E308"));
        assertEquals(DataType.INTEGER, typeDetector.detectType("2147483648")); // > Integer.MAX_VALUE but fits in Long
    }
}
package org.kie.pmml.models.drools.tree.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class IrisDataTreeTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "irisTree.pmml";
    private static final String MODEL_NAME = "IrisTreeModel";
    private static final String TARGET_FIELD = "Predicted_Species";
    private static PMMLRuntime pmmlRuntime;

    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;
    private String expectedResult;

    public void initIrisDataTreeTest(double sepalLength, double sepalWidth, double petalLength,
                            double petalWidth, String expectedResult) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9, 3.1, 5.1, 2.3, "virginica"},
                {5.8, 2.6, 4.0, 1.2, "versicolor"},
                {5.7, 3.0, 4.2, 1.2, "versicolor"},
                {5.0, 3.3, 1.4, 0.2, "setosa"},
                {5.4, 3.9, 1.3, 0.4, "setosa"}
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testIrisTree(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String expectedResult) {
        initIrisDataTreeTest(sepalLength, sepalWidth, petalLength, petalWidth, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Sepal.Length", sepalLength);
        inputData.put("Sepal.Width", sepalWidth);
        inputData.put("Petal.Length", petalLength);
        inputData.put("Petal.Width", petalWidth);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}

package io.github.pjazdzyk.hvaclib.properties;

public interface HumidGas extends Fluid {
    double getRH();
    double getHumRatioX();
    double getDewPointTemp();
}

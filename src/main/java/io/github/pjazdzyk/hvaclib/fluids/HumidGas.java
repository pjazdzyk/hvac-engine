package io.github.pjazdzyk.hvaclib.fluids;

public interface HumidGas extends Fluid {
    double getRH();
    double getHumRatioX();
    double getDewPointTemp();
}

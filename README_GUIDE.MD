## HVAC|LIB - Psychrometrics Analysis Library.<br> Thermodynamics of Humid Air for MEP/HVAC Engineers.<br>
### USER GUDE

> Author: **Piotr Jażdżyk**, M.Sc.Eng.<br>
> LinkedIn: https://www.linkedin.com/in/pjazdzyk <br>
---

#### [HOME](README.MD) &nbsp;|&nbsp; [AIR PROPERTIES](#air-properties) &nbsp;|&nbsp; [WATER PROPERTIES](#liquid-water-properties) &nbsp;|&nbsp; [FLOW OF FLUID](#flow-of-fluid) &nbsp;|&nbsp; [HEATING](#air-heating) &nbsp;|&nbsp; [COOLING](#air-cooling) &nbsp;|&nbsp; [AIR MIXING](#air-mixing) |&nbsp; [SYMBOLS](#symbols-and-abbreviations)

---

HVAC|LIB library is composed of two main packages: 'fluids' and 'process.' The 'fluids' package comprises algorithms for 
calculating properties of dry air, humid air, water vapor, liquid water, and solid ice. The 'process' package includes 
calculation procedures for obtaining results related to various thermodynamic processes typical in the HVAC industry, 
such as heating, cooling, and mixing. Additional processes will be incorporated in the upcoming stages of development.<br>
Thermophysical and thermodynamic properties are readily available through a collection of static methods based on 
primitive types or by using Utility's physical units value objects. Public access to primitive types is provided to 
facilitate iterative calculations, enhancing speed and reducing memory requirements.<br>
For developing your own application using this library, dedicated classes are available to represent the thermodynamic 
state of fluids with all associated calculated parameters, along with proper input argument validation and physical units.

### AIR PROPERTIES
Air properties, whether for dry air or humid air, can be calculated independently using the provided equations or as 
a thermodynamic state representing all properties based on the given temperature, pressure, and humidity for humid air.
Below is an example of direct property calculation for the humidity ratio of humid air using primitive argument types 
and physical units.

```java
// Outputs: 0.00754
double w_p = HumidAirEquations.humidityRatio(45, 2662, 100_000);

// Outputs: HumidityRatio{0.00754 kg/kg}
HumidityRatio w_u = HumidAirEquations.humidityRatio(
       RelativeHumidity.ofPercentage(45),
       Pressure.ofPascal(2662),
       Pressure.ofPascal(100_000)
);
```
Methods utilizing primitive types are designed for iterative calculations to minimize server memory usage and CPU time
consumption. However, for non-intensive workloads, it is advisable to employ Utility types as arguments to specify input
values in any of the supported units:

```java
// Outputs: Pressure{2338.8 Pa}
Pressure ps_c = HumidAirEquations.saturationPressure(Temperature.ofCelsius(20));
// Outputs: Pressure{2338.8 Pa}
Pressure ps_f = HumidAirEquations.saturationPressure(Temperature.ofFahrenheit(68));
```
Both methods will produce the same value regardless of the unit provided as an argument. By default, the resulting value 
is maintained in the predefined unit, but it can be readily converted to any other unit or to a value in the desired unit. 
For example:

```java
// Outputs: Pressure{100000 Pa}
Pressure pressureInPa = Pressure.ofPascal(100_000);
// Outputs: Pressure{1.0 bar}
Pressure pressureInBar = pressureInPa.toBar();
// Outputs: 1.0
double pressureValueInBar = pressureInPa.getInBar();
```
For more detailed information on handling physical units, please refer to the [UNITILITY - User Guide](https://github.com/pjazdzyk/unitility).<br>
To model the air state based on provided initial conditions, there are dedicated classes available for each supported 
fluid type. An instance of these classes encompasses all available parameters calculated for the provided input values,
including pressure, temperature, and humidity. Here's an example of an object representing the state of humid air under 
typical summer outdoor conditions

```java
HumidAir summerAir = HumidAir.of(
        Pressure.STANDARD_ATMOSPHERE,
        Temperature.ofCelsius(30),
        RelativeHumidity.ofPercentage(45)
);
```
For users or developers utilizing the console for result output, we have provided a dedicated method, _toFormattedString()_, 
which will display the air state in a readable and structured format. This method also automatically rounds values to four relevant digits

```text
HumidAir:
	P_abs = 101325 Pa | DBT = 30 °C | RH = 45 % | x = 0.01195 kg/kg | x" = 0.0272 kg/kg
	Ps = 4246.03 Pa | WBT = 16.776 °C | TDP = 21.053 °C | Vapour status: UNSATURATED
	i = 60.72 kJ/kg | ρ = 1.142 kg/m³ | cp = 1.027 kJ/(kg·K)
	ν = 0.00001608 m²/s | μ = 0.00001837 kg/(m·s) | k = 0.02709 W/(m·K)
	α = 0.00002308 m²/s | Pr = 0.6967 
	DryAir:
	P_abs = 101325 Pa | DBT = 30 °C
	i_da = 30.156 kJ/kg | ρ_da = 1.164 kg/m³ | cp_da = 1.005 kJ/(kg·K)
	ν_da = 0.00001592 m²/s | μ_da = 0.00001854 kg/(m·s) | k_da = 0.0267 W/(m·K) | Pr_da = 0.6979 
```
Please note that an instance of DryAir is a subcomponent of HumidAir. The property symbols used in this context adhere 
to commonly accepted practices in the engineering field. However, if you have any doubts, you can refer to the [SYMBOLS](#symbols-and-abbreviations)
section for clarification.<br>
Using the DryAir state class is analogous, with the distinction that humidity is not required as an argument.

### LIQUID WATER AND ICE PROPERTIES
Water properties can be computed directly using the equation class or initiated as a liquid water state with specific 
temperature and pressure values. It's important to note that, at this stage, all liquid properties are not considered
pressure-dependent, which is sufficient for psychrometric calculations. However, it's possible that this feature will 
be added in the future. Please be aware that water state can only be created for positive temperatures. <br>
Here's an example of calculating liquid water density based on temperature

```java
// Outputs: 998.884
double rho_val = LiquidWaterEquations.density(15);
// Outputs: Density{998.884 kg/kg}
Density rho = LiquidWaterEquations.density(Temperature.ofCelsius(15));
```

Example of creating liquid water state for a condensate:

```java
LiquidWater condensate = LiquidWater.of(Temperature.ofCelsius(30));
```

Using toFormattedString() method will result in following output:

```text
LiquidWater:
	P_abs = 101325 Pa | t_w = 30 °C
	i_w = 125.403 kJ/kg | ρ_w = 995.29 kg/m³ | cp_w = 4.18 kJ/(kg·K)
```

For the calculation of ice mist enthalpy, we have also provided a class to represent the ice state and its properties:

```java
// Outputs: 1.9995
IceEquations.specificHeat(-10);
// SpecificHeat{1.9995 kJ/(kg·K)}
IceEquations.specificHeat(Temperature.ofCelsius(-10));
```
Or using Ice state class:
```java
Ice.of(Temperature.ofCelsius(-11));
```

Resulting formatted output:
```text
Water ice:
	P_abs = 101325 Pa | t_ice = -11 °C
	i_ice = -356.035257 kJ/kg | ρ_ice = 918.831 kg/m³ | cp_ice = 1.994 kJ/(kg·K)
```

### FLOW OF FLUID
Supported fluids have dedicated classes to represent the fluid flow. An instance of this class is constructed by 
providing an instance of a fluid and one of the flow components, such as mass flow or volumetric flow. Based on this 
input, all other flows are calculated using the fluid properties. The Flow class encapsulates the most relevant properties 
from a composite Fluid, including temperature, pressure, and others. The Flow class is a crucial component for psychrometric
process algorithms. <br>
Below is an example of how to create an instance representing the flow of humid air:

```java
HumidAir winterAir = HumidAir.of(Temperature.ofCelsius(-20), HumidityRatio.ofKilogramPerKilogram(0.001));
FlowOfHumidAir.of(winterAir, VolumetricFlow.ofCubicMetersPerHour(5000));
```

We have specified the flow in m3/h, and there's no need to concern ourselves with unit conversions because, internally,
the Unitility framework ensures that values are always correctly converted to the appropriate units for calculations. 
For psychrometric calculations involving humid air, we require mass flow, volumetric flow, and dry air mass flow, and 
all of these flows are calculated and stored within the Flow instance.
Below is an example of how the console output for the Flow instance defined above: <br>

```text
FlowOfHumidAir:
	G = 1.933 kg/s | G = 6959.496 kg/h | V = 1.389 m³/s | V = 5000 m³/h
	G_da = 1.931 kg/s | G_da = 6952.544 kg/h
	HumidAir:
	P_abs = 101325 Pa | DBT = -20 °C | RH = 100 % | x = 0.001 kg/kg | x" = 0.0006345 kg/kg
	Ps = 103.26 Pa | WBT = -20 °C | TDP = -20 °C | Vapour status: ICE_FOG
	i = -18.63307 kJ/kg | ρ = 1.392 kg/m³ | cp = 1.005 kJ/(kg·K)
	ν = 0.00001154 m²/s | μ = 0.00001607 kg/(m·s) | k = 0.02282 W/(m·K)
	α = 0.00001632 m²/s | Pr = 0.7075 
	DryAir:
	P_abs = 101325 Pa | DBT = -20 °C
	i_da = -20.06 kJ/kg | ρ_da = 1.394 kg/m³ | cp_da = 1.003 kJ/(kg·K)
	ν_da = 0.00001153 m²/s | μ_da = 0.00001608 kg/(m·s) | k_da = 0.0228 W/(m·K) | Pr_da = 0.7075 
```
Result above includes all flow values and additionally humid air properties and humid air dry air component properties.
Handling flows for the dry air or liquid water is analogous.

### AIR HEATING
There are three humid air heating strategies:
- based on input heating power,
- based on target output temperature,
- based on target relative humidity

Let's assume typical winter case, when we want to heat up outdoor air up to +16 °C. First we need to create initial state 
of winter humid air, then select correct heating strategy to create heating process result object.

```java
// Creating initial state of ambient air
HumidAir winterAmbientAir = HumidAir.of(Temperature.ofCelsius(-20), RelativeHumidity.ofPercentage(99));
// Initializing flow instance
FlowOfHumidAir flowOfHumidAir = FlowOfHumidAir.of(winterAmbientAir, VolumetricFlow.ofCubicMetersPerHour(5000));
// Defining target outlet temperature
Temperature targetOutTemperature = Temperature.ofCelsius(16);
// Creating heating process, Q_heat = 69928.557 W
Heating heatingProcessState = Heating.of(HeatingStrategy.of(flowOfHumidAir, targetOutTemperature));
```
The heating process, upon creation, will invoke calculation procedures stored inside the selected strategy, ultimately
determining the heating power. Like all other process classes, the Heating class encapsulates the most relevant outlet 
air properties, including temperature, pressure, humidity, enthalpy, and others, for simplified value extraction when 
necessary.<br>
For quick console output, you can use the toFormattedString() method:

```text
PROCESS OF HEATING:
	INPUT FLOW:
	V_in = 5000 m³/h | G_in = 1.934 kg/s | G_in.da = 1.933 kg/s
	DBT_in = -20 °C | RH_in = 99 % | x_in = 0.0006281 kg/kg | i = -18.5124 kJ/kg
	HEAT OF PROCESS:
	Q_heat = 69928.557 W
	OUTLET FLOW:
	V_out = 5711.041 m³/h | G_out = 1.934 kg/s | G_out.da = 1.933 kg/s
	DBT_out = 16 °C | RH_out_out = 5.622 % | x_out_out = 0.0006281 kg/kg | i_out = 17.661 kJ/kg
```

As you can observe, resulting volumetric flow is greater than inlet flow. This is because the density of heated is lower
than cold ambient air resulting in increased volume of air after the heater. Mass flow on the other hand, as expected, 
remained unchanged.

### AIR COOLING
There are two main procedures available: dry cooling and real cooling coil with condensate discharge.
There are following strategies cooling available:
- based on input heating power,
- based on target output temperature,
- based on target relative humidity

Dry cooling does not support the strategy of cooling by target relative humidity (RH), and its usage is similar to the
heating process. For real coil cooling, one additional step is required: Coolant data must be provided. Here's an example
of cooling the ambient summer air to a typical indoor temperature of 25°C. We assume supply and return temperatures 
in the coil as 7/12°C:

```java
HumidAir summerAir = HumidAir.of(Temperature.ofCelsius(35), RelativeHumidity.ofPercentage(45));
FlowOfHumidAir flowOfSummerAir = FlowOfHumidAir.of(summerAir, VolumetricFlow.ofCubicMetersPerHour(5000));
CoolantData chilledWater = CoolantData.of(Temperature.ofCelsius(7), Temperature.ofCelsius(12));
Temperature targetOutTemp = Temperature.ofCelsius(25);
Cooling coolingProcess = Cooling.of(CoolingStrategy.of(flowOfSummerAir, chilledWater, targetOutTemp));
```

This process will result in following output:

```text
PROCESS OF COOLING:
	INPUT FLOW:
	V_in = 5000 m³/h | G_in = 1.551 kg/s | G_in.da = 1.527 kg/s
	DBT_in = 35 °C | RH_in = 45 % | x_in = 0.01594 kg/kg | i_in = 76.105 kJ/kg
	COOLANT DATA:
	t_su = 7 °C | t_rt = 12 °C | t_m = 9.5 °C
	HEAT OF PROCESS:
	Q_cool = -28.5261 kW | BF = 0.6078 
	OUTLET FLOW:
	V_out = 4812.259 m³/h | G_out = 1.551 kg/s | G_out.da = 1.532 kg/s
	DBT_out = 25 °C | RH_out = 63.403 % | x_out = 0.01258 kg/kg | i_out = 57.18 kJ/kg
	CONDENSATE:
	G_cond = 0.00513 kg/s | t_cond = 9.5 °C | i_cond = 39.866 kJ/kg
```

It can be observed that the air was successfully cooled to the expected temperature while also experiencing partial 
dehumidification. Additionally, the condensate flow has been calculated, along with the cooling power and coil bypass
factor. It's worth noting that the outlet volumetric flow is smaller than the inlet flow due to the lower density after 
cooling, but mass flow is conserved.

### AIR MIXING
There are two strategies available for mixing process:
- mixing inlet flow with one recirculation flow
- mixing inlet flow with multiple recirculation flows

Let's take a typical industry case, where we mix ambient external winter air in mixing plenum
with air inside a typical heated production hall:

```java
HumidAir winterAmbientAir = HumidAir.of(Temperature.ofCelsius(-20), RelativeHumidity.ofPercentage(99));
FlowOfHumidAir inletWinterFlow = FlowOfHumidAir.of(winterAmbientAir, VolumetricFlow.ofCubicMetersPerHour(1000));
HumidAir recirculationAir = HumidAir.of(Temperature.ofCelsius(16), RelativeHumidity.ofPercentage(25));
FlowOfHumidAir flowOfRecirculationAir = FlowOfHumidAir.of(recirculationAir, VolumetricFlow.ofCubicMetersPerHour(5000));
Mixing mixingProcess = Mixing.of(MixingStrategy.of(inletWinterFlow, flowOfRecirculationAir));
```

This process will result in following output:

```text
PROCESS OF MIXING:
	INPUT FLOW:
	V_in = 1000 m³/h | G_in = 0.3869 kg/s | G_suffix.da = 0.3866 kg/s
	DBT_in = -20 °C | RH_in = 99 % | x_in = 0.0006281 kg/kg | i_in = -18.5124 kJ/kg
	RECIRCULATION AIR_0:
	V_rec_0 = 5000 m³/h | G_rec_0 = 1.688 kg/s | G_suffix.da = 1.683 kg/s
	DBT_rec_0 = 16 °C | RH_rec_0 = 25 % | x_rec_0 = 0.002803 kg/kg | i_rec_0 = 23.165 kJ/kg
	OUTLET FLOW:
	V_out = 5999.992 m³/h | G_out = 2.074 kg/s | G_suffix.da = 2.07 kg/s
	DBT_out = 9.305 °C | RH_out = 33.188 % | x_out = 0.002397 kg/kg | i_out = 15.379 kJ/kg
```

As expected, the resulting temperature is approximately 9°C, including all other parameters of the outlet flow.

### SYMBOLS AND ABBREVIATIONS

Property symbols used in console formatted values:

| Symbol | Description                       |
|--------|-----------------------------------|
| P_abs  | Absolute pressure                 |
| DBT    | Dry bulb temperature              |
| WBT    | Wet bulb temperature              |
| TDP    | Dew point temperature             |
| RH     | Relative humidity                 |
| x      | Humidity ratio                    |
| Ps     | Saturation pressure               |
| i      | Specific enthalpy                 |
| ρ      | Density                           |
| cp     | Specific heat (constant pressure) |
| ν      | Kinematic viscosity               |
| μ      | Dynamic viscosity                 |
| k      | Thermal conductivity              |
| α      | Thermal diffusivity               |
| Pr     | Prandtl number                    |
| t_su   | Supply temperature                |
| t_rt   | Return temperature                |
| t_m    | Average temperature               |

Commonly used abbreviations:

| Suffix | Description  |
|--------|--------------|
| da     | Dry air      |
| w      | Liquid water |
| wv     | Water vapor  |
| ha     | Humid air    |
| ice    | Water ice    |


Water vapour status:

| Type        | Description                                                    |
|-------------|----------------------------------------------------------------|
| UNSATURATED | Water in the form of unsaturated vapour (x < x.max)            |
| SATURATED   | Water in the form of saturated vapour (x = x.max)              |
| WATER_MIST  | Water partially in the form of water mist (x > x.max, DBT > 0) |
| ICE_FOG     | Water partially in the form of ice fog (x > x.max, DBT <= 0)   |
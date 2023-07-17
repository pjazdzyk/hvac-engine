## HVAC-LIB - Psychrometrics Analysis Library <br> Thermodynamics of humid air for MEP/HVAC engineers <br> USER GUIDE

---

### [HOME](README.MD) &nbsp;|&nbsp; [MOIST AIR](#moist-air-thermophysical-properties) &nbsp;|&nbsp; [LIQUID WATER](#liquid-water-thermophysical-properties) &nbsp;|&nbsp; [FLOW HANDLING](#flow-of-moist-air) &nbsp;|&nbsp; [HEATING & COOLING](#heating-and-cooling-with-condensate-discharge) &nbsp;|&nbsp; [AIR MIXING](#mixing-of-two-moist-air-flows)

---

Please find below simple user guide for most relevant current functionalities. Please consider following naming
convention:<br>

* "Ma" - moist air
* "Da" - dry air
* "Wv" - water vapour

## MOIST AIR THERMOPHYSICAL PROPERTIES

MoistAir is a class representing a set of thermophysical properties ot air and water vapor mixture within typical
pressures in the HVAC industry.
Variables could be divided into core parameters which are: pressure, temperature, relative humidity or humidity ratio,
and dependent parameters,
which are already listed in the readme file or JavaDoc. Properties calculation equations are grouped in separate
classes with "Physics" prefix. These classes are meant to be used similarly as Java.util Math class, but for physics of
fluids. This allows for quick value calculation in any intermediate calculations without computational expensive creation of
instances.
For some values where a direct calculation is not possible,
the [Modified Brent Solver](https://github.com/pjazdzyk/brent-dekker-solver) is
used for quick and efficient iterative calculations, what allows to solve complex and nested property equations.

Naming convention in Physics classes is reduced and simplified to maintain name similarity to actual variable symbols in
literature or scientific papers.

Below is an example, which presents how to directly use calculation methods from Physics classes:

```java
// Saturation pressure calculation for air temperature of 20 oC:
double saturationPressure = LibPhysicsOfAir.calcMaPs(20);
System.out.println(saturationPressure); // Outputs 2338.80 Pa
```

For flow calculation or heating/cooling process analysis, an instance of MoistAir is required.
Let's create an object that represents thermophysical properties of air for typical summer design values: ta=30oC,
RH=45%.

```java
HumidGas summerAir = new MoistAir(30, 45, 90000, MoistAir.HumidityInputType.REL_HUMID);
```

For increased readability and ease of use the Builder Pattern implementation has been provided. Additionally, a set of
overloaded static "of" methods are provided for user convenience. You can configure as many parameters as you want.
If a given parameter is not provided - default values listed in LibDefaults will be used automatically, hence you can
simply invoke only build() and use setters to specify values later.

```java
// Using Builder Pattern
HumidGas summerAir = new MoistAir.Builder()
        .withAirTemperature(30)
        .withRelativeHumidity(45)
        .build();

// Static factory methods
HumidGas exampleAir1 = MoistAir.ofAir(30, 45);
HumidGas exampleAir2 = MoistAir.ofAir(30, 45, 90000);
```

For a quick review of all thermophysical parameters just invoke toString(). Each property is also available by provided
get() methods.

```java
System.out.println(summerAir);
```

This will output:

```
Core parameters  : Pat=101325 Pa | ta=30.000 degC | RH_Ma= 45.000 % | Wbt_Ma=21.053 degC | Tdp_Ma=16.776 degC | Ps= 4246.03 Pa | x_Ma= 0.011954 kg/kg | xMax= 0.027203 kg/kg 
Dry air          : rho_Da= 1.164 kg/m3 | cp_Da= 1.0052 kJ/kgK | i_Da= 30.16 kJ/kg.da 
Water vapour     : rho_Wv= 0.711 kg/m3 | cp_Wv= 1.8655 kJ/kgK | i_Wv= 2556.86 kJ/kg.da | i_Wt= 125.40 kJ/kg.da | i_Ice= 0.00 kJ/kg.da 
Moist air        : rho_Ma= 1.142 kg/m3 | cp_Ma= 1.0275 kJ/kgK | k_Ma= 0.0271 W/(m*K) | thDiff_Ma= 0.00002308 m2/s | dynVis_Ma = 0.00001837 kg/(m*s) | kinVis_Ma=0.0000161 m2/s | Pr_Ma=0.70 | i_Ma= 60.72 kJ/kg.da 
```

## LIQUID WATER THERMOPHYSICAL PROPERTIES

LiquidWater class is simpler, and it requires only the temperature and pressure to initialize properties. This class was created
mainly for modeling condensate purposes. Despite the required pressure input - at present development stage, water properties are not calculated as
pressure dependent, this might come as a feature in later development.<br>
To create a liquid water instance, just use a constructor or the builder pattern:

```java
// Creating Liquid water instance
Fluid waterCondensateExample1 = new LiquidWater(90000, 10);
Fluid waterCondensateExample2 = new LiquidWater.Builder()
           .withTemperature(10)
           .withPressure(90000)
           .build();
```

To get fluidOld resulting properties just use getters, or print everything via toString();

```
// Outputs
Core parameters  : ta=10.000 oC | cp=4.196 kJ/kgK | rho= 999.546 kg/m3 | ix=41.955 kJ/kg 
```

## FLOW OF MOIST AIR

FlowOfMoistAir instance calculates and stores mass flow and volumetric flow for moist air and its dry air component. All
of these flows depend on each other. They are calculated only once accordingly based on provided flow rate and type of flow upon
instance creation. Flow instance requires specific fluidOld which must be provided as an argument in constructor.

Creating a flow of moist air
with volumetric flow 5000m3/h of temperature ta=20oC and relative humidity RH=50%. For simplicity and ease of use
the best way is to use the Builder pattern:

```java
// Creating flow of moist air instance
double airTemp = 20; // oC
double airRH = 50;   // %
double moistAirVolFLow = 5000d / 3600d; // m3/s
HumidGas airExample = new MoistAir.Builder()
        .withAirTemperature(airTemp)
        .withRelativeHumidity(airRH)
        .build();
FlowOfHumidGas flowOfAir = new FlowOfMoistAir.Builder(airExample)
        .withVolFlowMa(moistAirVolFLow)
        .build();
```       

Or even simpler, by means of static factory method which takes volumetric flow in m3/h (most popular hvac unit) as an argument
and MoistAir instance:

```java                                         
FlowOfHumidGas flowOfAirExample = FlowOfMoistAir.ofM3hVolFlow(airExample, 5000);                                                        
```

To check all flows or parameters getters can be user, or we can print all via toString():

```
//Outputs:
Air properties: ta = 20.00 oC |	RH = 50.00 % | x = 0.00726 kg.wv/kg.da | status = UNSATURATED 
m_Ma = 1.653 kg/s moist air mass | flowv_Ma = 1.389 m3/s moist air vol flow | v_Ma = 5000.0 m3/s  moist air vol flow
m_Da = 1.641 kg/s dry air mass flow | v_Da 1.363 m3/s  dry air vol flow | v_Da = v_Da 4905.9 m3/s  dry air vol flow
```

As you can observe, we have specified volumetric flow, and all other flows were automatically calculated. <br>
In HVAC discipline, dry air mass flow is required for psychrometrics calculation, volumetric moist airflow is needed for equipment
capacity sizing or for final room ACH calculation.
Each application may need a different flow type, this is why this class combines all of them.

## FLOW OF FLUID

FlowOfFluid simpler compared to the above. It is designed to model a single-phase flow of any fluidOld class
implementing the Fluid interface. At the moment LiquidWater is provided to implement condensate discharge. Examples how
FlowOfFluid can be created as shown below. Similarly, as in case of flow of humid air, FlowOfFluid requires
a Fluid instance to be created prior to instantiating the flow:

```java
// Creating flow of fluidOld instance
double temp = 10.0; // oC
double volFlow = 1.2; // m3/s
LiquidWater waterExample = new LiquidWater.Builder()
            .withTemperature(10)
            .build();
// We did not specify pressure, so the builder will assume default value.
FlowOfFluid<LiquidWater> condensateFlow = new FlowOfSinglePhase.Builder<>(waterExample)
            .withVolFlow(volFlow)
            .build();
```

## HEATING AND COOLING WITH CONDENSATE DISCHARGE

The psychrometric process of heating or cooling is based on the flow of most air. This process requires an input
flow. Output flow is the result of an applied process and will be created as the result of invoking runProcess() method.
Additionally, for the cooling process, there is a third part: the flow of liquid water representing the condensate
discharge, which also will be created during process execution.<br>
To create the heating and cooling process class just use the constructor and pass FlowOfMoistAir instances as an input
and output flow. You can specify only the input flow, in this case,
output flow will be created automatically as an independent object cloned from the input flow. Here is some example:
let's try to heat 5000 m3/h of winter air of -20 oC / RH = 95% to the target temperature of +18oC:

```java
// Heating process example
double targetTemp = 18.0; // oC
// Step 1: Creating moist air instance
HumidGas winterAmbientAir = MoistAir.ofAir(-20, 99, 101_325);
// Step 3: creating humid air flow
FlowOfHumidGas inletAir = FlowOfMoistAir.ofM3hVolFlow(winterAmbientAir, 5000);
// Step 4: creating heating process
ProcessHeatDriven heating = new HeatingForTargetTemp(inletAir, targetTemp);
heating.runProcess();
````

Process summary can be easily shown by invoking toString(). Output air instance can be also accessed by provided
getters. Here is a process result summary:

```
// Outputs:
-------------- PROCESS TYPE: HeatingForTargetTemp --------------
INLET FLOW DATA:
Air properties: 
ta = -20.00 oC | RH = 99.00 % | x = 0.00063 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 1.934 kg/s (moist air mass flow) | v_Ma = 1.389 m3/s (moist air vol flow) |v_Ma = 5000.0 m3/s (moist air vol flow) 
m_Da = 1.933 kg/s (dry air mass flow) | v_Da 1.386 m3/s (dry air vol flow) | v_Da = v_Da 4991.0 m3/s (dry air vol flow)

OUTLET FLOW DATA:
Air properties: 
ta = 18.00 oC | RH = 4.95 % | x = 0.00063 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 1.934 kg/s (moist air mass flow) | v_Ma = 1.597 m3/s (moist air vol flow) |v_Ma = 5750.5 m3/s (moist air vol flow) 
m_Da = 1.933 kg/s (dry air mass flow) | v_Da 1.595 m3/s (dry air vol flow) | v_Da = v_Da 5740.2 m3/s (dry air vol flow)

PROCESS SPECIFIC DATA:
Q = 73820.392 W (heat of process) 
Q = 73.82 kW (heat of process)
```

From the summary above we can see that the required heating power is around 73,8kW and output air property changes from
-20oC/95% to 18.0 oC/4.8% what is exactly what we would expect.<br>
<br>
The cooling process is more sophisticated. To properly calculate resulting cooling power or an outlet air properties, we
must include the fact that some fraction of air in direct contact of cooling coil wall will be cooled down to approximately 
the average wall temperature and the water condensate will be discharged consuming a significant amount of cooling power for 
its phase change. As the result - this air will be mixed with part of the air which is not in direct coil wall contact. This means
that the result is strongly dependent on coolant supply and return temperature, and resulting average wall temperature. 
The value of average cooling coil wall temp must be provided for calculation, and for a typical case it is acceptable to 
provide arithmetic average between coolant supply and return temperatures.
Let's try to simulate the cooling process for a typical air conditioning scenario: ambient air 32oC/50%, expected outlet air 
temperature as +24oC, volumetric flow = 5000m3/h. Coolant parameters: 8/14oC.

```java
double targetCoolingTemp = 24.0; // OC
double averageCoilWallTemp = MathUtils.calcArithmeticAverage(8, 14); // oC
// Step 1: Creating moist air instance
HumidGas summerAmbientAir = MoistAir.ofAir(32, 50, 90000);
// Step 3: creating humid air flow
FlowOfHumidGas summerAirFlow = FlowOfMoistAir.ofM3hVolFlow(summerAmbientAir, 5000);
// Step 4: creating heating process
ProcessHeatDriven cooling = new CoolingForTargetTemp(summerAirFlow, averageCoilWallTemp, targetCoolingTemp);
cooling.runProcess();
```

Let's see the results:

```
-------------- PROCESS TYPE: CoolingForTargetTemp --------------
INLET FLOW DATA:
Air properties: 
ta = 32.00 oC | RH = 50.00 % | x = 0.01689 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 1.389 kg/s (moist air mass flow) | v_Ma = 1.389 m3/s (moist air vol flow) |v_Ma = 5000.0 m3/s (moist air vol flow) 
m_Da = 1.366 kg/s (dry air mass flow) | v_Da 1.330 m3/s (dry air vol flow) | v_Da = v_Da 4786.2 m3/s (dry air vol flow)

OUTLET FLOW DATA:
Air properties: 
ta = 24.00 oC | RH = 66.20 % | x = 0.01396 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 1.385 kg/s (moist air mass flow) | v_Ma = 1.342 m3/s (moist air vol flow) |v_Ma = 4832.7 m3/s (moist air vol flow) 
m_Da = 1.366 kg/s (dry air mass flow) | v_Da 1.295 m3/s (dry air vol flow) | v_Da = v_Da 4660.7 m3/s (dry air vol flow)

CONDENSATE FLOW DATA:
Fluid properties: 
ta = 11.00 oC | rho = 999.44 kg/m3 | fluidOld class = LiquidWater 
Flow properties: 
m = 0.00400 kg/s (fluidOld mass flow) | v = 0.00000400 m3/s (fluidOld vol flow) | v = 0.014 m3/h (fluidOld vol flow) 

PROCESS SPECIFIC DATA:
Q = -21232.738 W (heat of process)
Q = -21.23 KW (heat of process)
BF = 0.619 [-] (coil by-pass factor)
tm = 11.000 oC (average cooling coil wall temperature)
```

Based on the results above: the required cooling power for the process is 21,2kW. Airflow properties have changed from
32oC/50% to 24oC/66.2%. Average wall temperature (tm_wall) and bypass factors were calculated based on the provided input data.<br>
If for any reason you would like to use dry cooling process without specifying coil details, you can find methods for this type of process
inside the PhysicsOfCooling class.

LIMITATIONS:<br>
A typical HVAC temperature range should be used. Achieving negative temperatures for cooling is not possible at the
moment, ice buildup is not included in the process.
At this stage of development, this functionality is dedicated to air conditioning, not industrial deep cooling/freezing.

## MIXING OF TWO MOIST AIR FLOWS

The mixing process is designed to allow the mixing of two or more moist air flows. All parameters are recalculated
accordingly to determine the resulting output airflow. Number of streams to be mixed is practically unlimited.
Typical usage is to model the AHU recirculation section, where ambient intake is mixed with recirculated air from the room
to reduce the heat output of the heating coil if the amount of fresh air could be reduced.<br>
For the purpose of this presentation, lets try to mix four different air flows together:

```java
// Mixing process example
// Step 1: Creating inlet flow
HumidGas coldAmbientAir = MoistAir.ofAir(-20, 99, 101_325);
FlowOfHumidGas inletAirFlow = FlowOfMoistAir.ofM3hVolFlow(coldAmbientAir, 5000);
// Step 2: Creating a couple of recirculation flows
HumidGas returnAir1 = MoistAir.ofAir(20, 30, 101_325);
FlowOfHumidGas returnFlow1 = FlowOfMoistAir.ofM3hVolFlow(returnAir1, 5000);
HumidGas returnAir2 = MoistAir.ofAir(15, 45, 101_325);
FlowOfHumidGas returnFlow2 = FlowOfMoistAir.ofM3hVolFlow(returnAir2, 2000);
HumidGas returnAir3 = MoistAir.ofAir(18, 30, 101_325);
FlowOfHumidGas returnFlow3 = FlowOfMoistAir.ofM3hVolFlow(returnAir3, 1000);
// Step 3: Creating air mixing process
ProcessWithMixing mixing = new MixingOfMoistAir(inletAirFlow, returnFlow1, returnFlow2, returnFlow3);
mixing.runProcess();
```

Mixing process output will be as shown below:

```
-------------- PROCESS TYPE: MixingOfMoistAir --------------
INLET FLOW DATA:
Air properties: 
ta = -20.00 oC | RH = 99.00 % | x = 0.00063 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 1.934 kg/s (moist air mass flow) | v_Ma = 1.389 m3/s (moist air vol flow) |v_Ma = 5000.0 m3/s (moist air vol flow) 
m_Da = 1.933 kg/s (dry air mass flow) | v_Da 1.386 m3/s (dry air vol flow) | v_Da = v_Da 4991.0 m3/s (dry air vol flow)

RECIRCULATION FLOW DATA:
Air properties: 
ta = 20.00 oC | RH = 30.00 % | x = 0.00434 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 1.661 kg/s (moist air mass flow) | v_Ma = 1.389 m3/s (moist air vol flow) |v_Ma = 5000.0 m3/s (moist air vol flow) 
m_Da = 1.653 kg/s (dry air mass flow) | v_Da 1.373 m3/s (dry air vol flow) | v_Da = v_Da 4943.2 m3/s (dry air vol flow)

Air properties: 
ta = 15.00 oC | RH = 45.00 % | x = 0.00475 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 0.675 kg/s (moist air mass flow) | v_Ma = 0.556 m3/s (moist air vol flow) |v_Ma = 2000.0 m3/s (moist air vol flow) 
m_Da = 0.672 kg/s (dry air mass flow) | v_Da 0.549 m3/s (dry air vol flow) | v_Da = v_Da 1975.2 m3/s (dry air vol flow)

Air properties: 
ta = 18.00 oC | RH = 30.00 % | x = 0.00382 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 0.335 kg/s (moist air mass flow) | v_Ma = 0.278 m3/s (moist air vol flow) |v_Ma = 1000.0 m3/s (moist air vol flow) 
m_Da = 0.333 kg/s (dry air mass flow) | v_Da 0.275 m3/s (dry air vol flow) | v_Da = v_Da 989.9 m3/s (dry air vol flow)

OUTLET FLOW DATA:
Air properties: 
ta = 2.36 oC | RH = 62.64 % | x = 0.00280 kg.wv/kg.da | status = UNSATURATED |
Flow properties: 
m_Ma = 4.605 kg/s (moist air mass flow) | v_Ma = 3.611 m3/s (moist air vol flow) |v_Ma = 12999.4 m3/s (moist air vol flow) 
m_Da = 4.592 kg/s (dry air mass flow) | v_Da 3.584 m3/s (dry air vol flow) | v_Da = v_Da 12903.1 m3/s (dry air vol flow)
```

This process does not assume any humidity discharge. If x > xMax, water fog or ice fog is assumed to be traveling with
air outside process boundary.

In the PhysicsOfMixing class you can find more advanced mixing algorithm, which allows fot define target output dry air mass flow,
and output temperature. Algorithm will not only calculate the resulting flow, but will attempt to adjust an inlet flow,
with respect of defined minimal flow limits. This might be helpful for analyzing of air heater with mixing plenum, to determine
appropriate recirculation ratio to achieve target supply parameters.

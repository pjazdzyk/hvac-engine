## DEW|POINT - Psychrometrics Analysis Tool <br> Thermodynamics of humid air API for MEP/HVAC engineers <br> USER GUIDE

---

<span style="font-weight:700;font-size:15px">

[HOME](README.MD) | [MOIST AIR](#MOIST AIR THERMOPHYSICAL PROPERTIES:) | [LIQUID WATER](#LIQUID WATER THERMOPHYSICAL PROPERTIES:) | [FLOW HANDLING](#LIQUID WATER THERMOPHYSICAL PROPERTIES:)

</span>

---

Please find below simple user guide for most relevant current functionalities. Please consider following naming convention:<br>
* "Ma" - moist air
* "Da" - dry air
* "Wv" - water vapour

### MOIST AIR THERMOPHYSICAL PROPERTIES:
MoistAir is a class representing a set of thermophysical properties ot air and water vapour mixture within a typical pressures in HVAC industry.
Variables could be divided on core parameters which are: pressure, temperature, relative humidity or humidity ratio, and dependent parameters,
which are already listed in readme file or javadoc. All properties' calculation equations are gathered in a separate classes with "Lib" prefix.
This allows for quick value obtaining in any intermediate calculations without computational expensive creation of MoistAir instance.
For some values where direct calculation is not possible, the [Modified Brent Solver](https://github.com/pjazdzyk/Brent-Decker-Solver) is 
used for quick and efficient iterative calculations. Use example of moist air property library is shown below:
```
//Saturation pressure calculation for a ta=20oC
var saturationPressure = LibPropertyOfAir.calc_Ma_Ps(20); 
System.out.println(saturationPressure); //Outputs 2338.80 Pa
```
For flow calculation or heating / cooling process analysis an instance of MoistAir is required.
Let's create an object that represent thermophysical properties of air for a typical summer design values: ta=30oC, RH=45%.
```
var summerAir = new MoistAir("summer", 30, 45);
```
By default, all pressure-dependent variables are calculated for atmospheric pressure pat=101325 Pa. It is possible to use overloaded constructor which allows for providing user defined atmospheric pressure and humidity type.
```
var summerAir2 = new MoistAir("summer2", 30, 45, 90000, MoistAir.HumidityType.REL_HUMID);
```
It is also possible to set a specific altitude above sea level, and application will correct pressure value accordingly:
```
summerAir2.setElevationASL(2000);
System.out.println(summerAir2.getPat()); //Outputs: 79495.12 Pa
```
For increased readability and ease of use the Builder Pattern implementation has been provided. Additionally, a set of overloaded static "of" methods are provided for user convenience. You can configure as many parameters as you want. 
If a given parameter is not provided - a default values listed in LibDefaults will be used automatically, hence you can simply invoke only build() and use setters to specify values later.
```
//Constructed from Builder pattern
var summerAir3 = new MoistAir.Builder()
                             .withName("Summer3")
                             .withTa(30).withRH(45)
                             .withZElev(2000)
                             .build();
                             
//Constructed from static ofAir() method.
var summerAir4 = MoistAir.ofAir(30,45);
```
For quick review of all thermophysical parameters just invoke toString(). Each property is also available by provided get() methods.
```
System.out.println(summerAir3.toString());
```
This will output:
```
Instance name 	 : Summer3
Core parameters  : Pat=79495 Pa | ta=30.000 degC | RH_Ma= 45.000 % | Wbt_Ma=20.416 degC | Tdp_Ma=16.776 degC | Ps= 4246.03 Pa | x_Ma= 0.011954 kg/kg | xMax= 0.035095 kg/kg 
Dry air          : rho_Da= 0.914 kg/m3 | cp_Da= 1.0052 kJ/kgK | k_Da= 0.0267 W/(m*K) | thDiff_Da= 0.00002908 m2/s | dynVis_Da = 0.00001854 kg/(m*s) | kinVis_Da=0.0000203 m2/s | Pr_Da=0.70 | i_Da= 30.16 kJ/kg.da 
Water vapour     : rho_Wv= 0.629 kg/m3 | cp_Wv= 1.8568 kJ/kgK | k_Wv= 0.0196 W/(m*K) | thDiff_Wv= 0.00001675 m2/s | dynVis_Wv = 0.00001002 kg/(m*s) | kinVis_Mv=0.0000159 m2/s | Pr_Wv=0.95 | i_Wv= 2556.60 kJ/kg.da | i_Wt= 125.70 kJ/kg.da | i_Ice= 0.00 kJ/kg.da 
Moist air        : rho_Ma= 0.896 kg/m3 | cp_Ma= 1.0274 kJ/kgK | k_Ma= 0.0271 W/(m*K) | thDiff_Ma= 0.00002941 m2/s | dynVis_Ma = 0.00001837 kg/(m*s) | kinVis_Ma=0.0000205 m2/s | Pr_Ma=0.70 | i_Ma= 60.72 kJ/kg.d
```

### LIQUID WATER THERMOPHYSICAL PROPERTIES:
LiquidWater class is far simpler, and it requires only the temperature to initialize properties. This class was created for modeling condensate purposes, and will be further extended.
To create liquid water instance, just use any of the constructor or builder pattern:
```
var water1 = new LiquidWater("water", 10);
var water2 = new LiquidWater.Builder().withName("water").withTa(10).build();
var water3 = LiquidWater.ofWater("water",10);
```
You can get water properties by appropriate get() methods. To check all parameters, just invoke toString():

```
var waterSpecEnthalpy = water1.getIx();
System.out.println(waterSpecEnthalpy); //Outputs: 41.95 kJ/kgK

System.out.println(water1);

Outputs:
Fluid name: water
Core parameters  : ta=10.000 oC | cp=4.196 kJ/kgK | rho= 999.546 kg/m3 | ix=41.955 kJ/kg 
```
### FLOW OF MOIST AIR
FlowOfMoistAir instance calculates and stores mass flow and volumetric flow for moist air and its dry air component. All of these flows depend on each other. Setting one type of the flow will automatically
calculate and set all others. Flow depends on moist air instance. It is important to emphasize that flows will not update automatically if a change emerges inside MoistAir aggregated instance caused by an external
action (ie: in case MoistAir reference is shared across other classes). For such event, updateFlows() method is provided. Let's try to create some flows and see how it works. Creating flow of moist air
with volumetric flow 5000m3/h of temperature ta=20oC and relative humidity RH=50%.
```
var ta = 20; //OC
var RH = 50; //%
var volFlowMa = 5000.0/3600.0; //m3/s
var air1 = new MoistAir("NewAir", 20, 50);
var flow1 = new FlowOfMoistAir("Test flow", volFlowMa, FlowOfMoistAir.AirFlowType.MA_VOL_FLOW,air1);
```
Please note that if you are using constructors for flow creation, you have to specify which flow type are you providing by use of Enum AirFlowType as shown above. For simplicity and ease of use
you can always use Builder pattern or quick static ofFlow() methods:

```
var flow2 = new FlowOfMoistAir.Builder()
                              .withTx(20).withRH(50)
                              .withVolFlowMa(5000.0/3600.0)
                              .withFlowName("Test flow");
                                  
var flow3 = FlowOfMoistAir.ofM3hVolFlow(5000,20,50);                                                           
```
Each way of instance creation will have the same result:
```
Flow name:	Test flow
Locked flow: 	MA_VOL_FLOW
m_Ma = 1.653 kg/s 	moist air mass flow	 | v_Ma = 1.389 m3/s 	moist air vol flow	 |  v_Ma = 5000.0 m3/h 	moist air vol flow
m_Da = 1.641 kg/s 	dry air mass flow	 | v_Da = 1.363 m3/s 	dry air vol flow	 |  v_Da = 4905.9 m3/h 	dry air vol flow
```
As you can observe, we have specified volumetric flow, and all other flows was calculated. <br>
In HVAC dry air mass flow is required for psychrometrics calculation, volumetric moist air flow is needed for equipment capacity sizing or for final room ACH calculation. 
Each application may need different flow type, this is wy this instance combine all of them.

"LOCKED FLOW" EXPLANATION:<br>
Flow classes are designed to be components of psychometric process classes or equipment systems classes. Setting any type of flow (dry air or moist air mass flow / vol flow) will automatically
set lockedFlow. Now, if the density of the aggregated moist air will change, other flows will be adjusted based on new air properties but with respect to the flow we want to keep as "locked". 
Here are some examples:<br>
Example 1: We want to use FlowOfMoistAir instance as a model of AHU fan, which is set to keep constant volumetric flow. Measurement instruments inside AHU will measure moist air flow,
therefore if the air temperature will change, mass flow will change but the volumetric flow will remain the same.
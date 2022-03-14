## DEW|POINT - Psychrometrics Analysis Tool <br> Thermodynamics of humid air API for MEP/HVAC engineers <br> USER GUIDE

[HOME](README.MD) | [MOIST AIR](#MOIST AIR THERMOPHYSICAL PROPERTIES:) | [LIQUID WATER](#LIQUID WATER THERMOPHYSICAL PROPERTIES:) | [FLOW HANDLING](#FLOW OF MOIST AIR:) | [HEATING & COOLING](#HEATING AND COOLING WITH CONDENSATE DISCHARGE:)

Please find below simple user guide for most relevant current functionalities. Please consider following naming convention:<br>
* "Ma" - moist air
* "Da" - dry air
* "Wv" - water vapour

### MOIST AIR THERMOPHYSICAL PROPERTIES:
MoistAir is a class representing a set of thermophysical properties ot air and water vapor mixture within typical pressures in the HVAC industry.
Variables could be divided into core parameters which are: pressure, temperature, relative humidity or humidity ratio, and dependent parameters,
which are already listed in the readme file or JavaDoc. All properties' calculation equations are gathered in separate classes with "Lib" prefix.
This allows for quick value obtaining in any intermediate calculations without computational expensive creation of MoistAir instance.
For some values where a direct calculation is not possible, the [Modified Brent Solver](https://github.com/pjazdzyk/Brent-Decker-Solver) is
used for quick and efficient iterative calculations. Use an example of moist air property library is shown below:
```
//Saturation pressure calculation for a ta=20oC
var saturationPressure = LibPropertyOfAir.calc_Ma_Ps(20); 
System.out.println(saturationPressure); //Outputs 2338.80 Pa
```
For flow calculation or heating/cooling process analysis, an instance of MoistAir is required.
Let's create an object that represents thermophysical properties of air for typical summer design values: ta=30oC, RH=45%.
```
var summerAir = new MoistAir("summer", 30, 45);
```
By default, all pressure-dependent variables are calculated for atmospheric pressure pat=101325 Pa. It is possible to use an overloaded constructor which allows for providing user-defined atmospheric pressure and humidity type.
```
var summerAir2 = new MoistAir("summer2", 30, 45, 90000, MoistAir.HumidityType.REL_HUMID);
```
It is also possible to set a specific altitude above sea level, and application will correct pressure value accordingly:
```
summerAir2.setElevationASL(2000);
System.out.println(summerAir2.getPat()); //Outputs: 79495.12 Pa
```
For increased readability and ease of use the Builder Pattern implementation has been provided. Additionally, a set of overloaded static "of" methods are provided for user convenience. You can configure as many parameters as you want. 
If a given parameter is not provided - default values listed in LibDefaults will be used automatically, hence you can simply invoke only build() and use setters to specify values later.
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
For a quick review of all thermophysical parameters just invoke toString(). Each property is also available by provided get() methods.
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
LiquidWater class is far simpler, and it requires only the temperature to initialize properties. This class was created for modeling condensate purposes and will be further extended.
To create a liquid water instance, just use any of the constructor or builder pattern:
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
### FLOW OF MOIST AIR:
FlowOfMoistAir instance calculates and stores mass flow and volumetric flow for moist air and its dry air component. All of these flows depend on each other. Setting one type of the flow will automatically
calculate and set all others. Flow depends on moist air instance. It is important to emphasize that flows will not update automatically if a change emerges inside MoistAir aggregated instance caused by an external
action (ie: in case MoistAir reference is shared across other classes). For such an event, updateFlows() method is provided. Let's try to create some flows and see how it works. Creating a flow of moist air
with volumetric flow 5000m3/h of temperature ta=20oC and relative humidity RH=50%.
```
var ta = 20; //OC
var RH = 50; //%
var volFlowMa = 5000.0/3600.0; //m3/s
var air1 = new MoistAir("NewAir", 20, 50);
var flow = new FlowOfMoistAir("Test flow", volFlowMa, FlowOfMoistAir.AirFlowType.MA_VOL_FLOW,air1);
```
Please note that if you are using constructors for flow creation, you have to specify which flow type are you providing by use of Enum AirFlowType as shown above. For simplicity and ease of use
you can always use the Builder pattern:

```
var flow = new FlowOfMoistAir.Builder()
                              .withTx(20).withRH(50)
                              .withVolFlowMa(5000.0/3600.0)
                              .withFlowName("Test flow");
                              .build();
```       
Or simpler, for a typical HVAC case, allowing for volumetric flow to be provided in m3/h:

```                                                   
var flow = FlowOfMoistAir.ofM3hVolFlow(5000,20,50);                                                           
```

Results of the calculations are stored in class property fields. You can easily peek the results:

```
System.out.println(flow);

//Outputs:

Flow name: 		Test flow
Locked flow: 		MA_VOL_FLOW
Air properties: 	ta = 20.00 oC 	RH = 50.00 % 	x = 0.00726 kg.wv/kg.da 	status = UNSATURATED
m_Ma = 1.653 kg/s 	moist air mass flow	 | v_Ma = 1.389 m3/s 	moist air vol flow	 |  v_Ma = 5000.0 m3/h 	moist air vol flow
m_Da = 1.641 kg/s 	dry air mass flow	 | v_Da = 1.363 m3/s 	dry air vol flow	 |  v_Da = 4905.9 m3/h 	dry air vol flow
```
As you can observe, we have specified volumetric flow, and all other flows were automatically calculated. <br>
In HVAC dry air mass flow is required for psychrometrics calculation, volumetric moist airflow is needed for equipment capacity sizing or for final room ACH calculation.
Each application may need a different flow type, this is why this class combines all of them.

"LOCKED FLOW" EXPLANATION:<br>
Locked flow defines which flow will have a "locked value" in case air property changes. This allows adjusting flow instance behavior depending on the application. When simulating an AHU inlet
fan, which control system attempts to keep constant moist air volumetric flow, we want to have the same volumetric flow kept no matter how air properties will change.
On the other hand, if we consider heating/cooling coil, we want to keep dry air mass flow locked for psychrometrics calculations and allow other flows to change accordingly to the moist air state.
This should be carefully considered in every case. By default, using a setter of any given type of flow automatically sets this flow type as locked.

### FLOW OF FLUID:
FlowOfFluid is more simple compared to the above. It is designed to model a single-phase flow of any fluid class implementing the Fluid interface. At the moment LiquidWater is provided to implement condensate discharge. Examples how
FlowOfFluid can be initiated as shown below:

```
//Using Constructor
var volFlow = 1.2; // m3/s
var condensate = new LiquidWater("condensate",10);
var condensateFlow1 = new FlowOfFluid("CondensateFlow", volFlow, FlowOfFluid.FluidFlowType.VOL_FLOW, condensate);
        
//Using Builder pattern
var condensateFlow2 = new FlowOfFluid.Builder<LiquidWater>(LiquidWater::new).withFlowName("CondensateFlow")
                                                                            .withVolFlow(volFlow)
                                                                            .withFluidInstance(condensate);
//Using of() methods for water in m3/h:
var volFlow1 = volFlow * 3600; //m3/h
var condensateFlow3 = FlowOfFluid.ofM3hWaterVolFlow(volFlow1, temp);
```
Please note that Builder class in FlowOfFluid is generic, therefore an extractor must be provided as the argument (a reference to the constructor of the specific Fluid class).

### HEATING AND COOLING WITH CONDENSATE DISCHARGE:
The heating and cooling of the moist air are modeled by the ProcAirHeatCool class. This class is based on psychrometric equations stored in LibPsychroProcess. This class allows determining the outlet moist air properties including the condensate flow and its properties for typical HVAC cooling or heating applications. The following functionality has been
developed so far:</br>
* air heating or cooling if input heating power is known,</br>
* air heating or cooling result if expected output temperature is known,</br>
* air heating or cooling result if the expected relative humidity is known.</br>

The psychrometric process of heating or cooling is based on the flow of most air. This process requires two flows: input flow and output flow. Output flow is the result of an applied process.
Additionally, for the cooling process, there is a third part: the flow of liquid water representing the condensate discharge.<br>
To create the heating and cooling process class just use the constructor and pass FlowOfMoistAir instances as an input and output flow. You can specify only the input flow, in this case,
output flow will be created automatically as an independent object cloned from the input flow. Here is some example: let's try to heat 5000m3/h of winter air of -20oC/RH=95% to the +18oC:
```
var inputFlow = FlowOfMoistAir.ofM3hVolFlow(5000,-20,95);
var heater = new ProcAirHeatCool(inputFlow);
var expectedOutTemp = 18.0; //oC
heater.applyHeatingInQFromOutTx(expectedOutTemp);
````
Process summary can be easily shown by invoking toString(). Output air instance can be also accessed by provided getters. Here is a process result summary:
```
System.out.println(heater);
//Outputs:
-------------------------------------HEATING/COOLING PROCESS-------------------------------------
>>INLET FLOW:
Flow name: 		New Flow
Locked flow: 		MA_VOL_FLOW
Air properties: 	ta = -20.00 oC 	RH = 95.00 % 	x = 0.00060 kg.wv/kg.da 	status = UNSATURATED
m_Ma = 1.934 kg/s 	moist air mass flow	 | v_Ma = 1.389 m3/s 	moist air vol flow	 |  v_Ma = 5000.0 m3/h 	moist air vol flow
m_Da = 1.933 kg/s 	dry air mass flow	 | v_Da = 1.386 m3/s 	dry air vol flow	 |  v_Da = 4991.4 m3/h 	dry air vol flow
tx_In = -20.00 oC 	inlet air temperature
RH_In = 95.00 % 	inlet air relative humidity
x_In = 0.00060 kgWv/kgDa 	inlet air humidity ratio

>>OUTLET FLOW:
Flow name: 		New Flow
Locked flow: 		DA_MASS_FLOW
Air properties: 	ta = 18.00 oC 	RH = 4.75 % 	x = 0.00060 kg.wv/kg.da 	status = UNSATURATED
m_Ma = 1.934 kg/s 	moist air mass flow	 | v_Ma = 1.597 m3/s 	moist air vol flow	 |  v_Ma = 5750.5 m3/h 	moist air vol flow
m_Da = 1.933 kg/s 	dry air mass flow	 | v_Da = 1.595 m3/s 	dry air vol flow	 |  v_Da = 5740.6 m3/h 	dry air vol flow
tx_Out = 18.00 oC 	inlet air temperature
RH_Out = 4.75 % 	inlet air relative humidity
x_Out = 0.00060 kgWv/kgDa 	inlet air humidity ratio

>>CONDENSATE FLOW:
Flow name: 		FlowOfFluid
Locked flow:            MASS_FLOW
m_Con = 0.000 kg/s 	condensate mass flow	 | v_Con = 0.000000 m3/s 	condensate vol flow	 |  v_Con = 0.000 m3/h 	condensate vol flow
tx_Con = 18.00 oC 	condensate temperature

>>HEAT OF PROCESS:
Q = 73821.55 W 	 heating / cooling power

>>COIL PROPERTIES:
ts_hydr = 6.00 oC 	 coolant supply temperature
tr_hydr = 12.00 oC 	 coolant return temperature
tm_wall = 9.00 oC 	 average linear wall temperature
BF = 0.000 - 		 coil bypass factor
-----------------------------------------END OF RESULTS-----------------------------------------
```
From the summary above we can see that the required heating power is around 73,8kW and output air property changes from -20oC/95% to 18.0oC/4.8%.
Please note that for the heating process the condensate flow and coil properties are not relevant and could be ignored. <br>
The cooling process is more sophisticated. To properly calculate resulting cooling power or outlet air properties, we must include the fact that some fraction of air
in direct contact of cooling coil wall will be cooled to approximately the average wall temperature and the water condensate will be discharged consuming a significant
amount of cooling power. Next, this air will be mixed with part of the air which is not in direct contact. This means that the result is strongly dependent on coolant supply and
return temperature, and resulting average wall temperature. This value must be properly provided to get a good quality result. By default, these temps are set as 6/12oC. Let's
try to simulate cooling for a typical air conditioning scenario: ambient air 32oC/50%, expected supply temperature +24oC, volumetric flow = 5000m3/h. Coolant parameters: 8/14oC.
```
var inputSummerFlow = FlowOfMoistAir.ofM3hVolFlow(5000,32,50);
var coolingCoil = new ProcAirHeatCool(inputSummerFlow);
var expectedOutSupplyTemp = 24.0; //oC
var coolantSupTemp = 8.0; //oC
var coolantRetTemp = 14.0; //oC
coolingCoil.setTs_Hydr(coolantSupTemp);
coolingCoil.setTr_Hydr(coolantRetTemp);
coolingCoil.applyCoolingInQFromOutTx(expectedOutSupplyTemp);
```
Let's see the results:
```
-------------------------------------HEATING/COOLING PROCESS-------------------------------------
>>INLET FLOW:
Flow name: 		New Flow
Locked flow: 		MA_VOL_FLOW
Air properties: 	ta = 32.00 oC 	RH = 45.00 % 	x = 0.01343 kg.wv/kg.da 	status = UNSATURATED
m_Ma = 1.572 kg/s 	moist air mass flow	 | v_Ma = 1.389 m3/s 	moist air vol flow	 |  v_Ma = 5000.0 m3/h 	moist air vol flow
m_Da = 1.552 kg/s 	dry air mass flow	 | v_Da = 1.341 m3/s 	dry air vol flow	 |  v_Da = 4828.7 m3/h 	dry air vol flow
tx_In = 32.00 oC 	inlet air temperature
RH_In = 45.00 % 	inlet air relative humidity
x_In = 0.01343 kgWv/kgDa 	inlet air humidity ratio

>>OUTLET FLOW:
Flow name: 		New Flow
Locked flow: 		DA_MASS_FLOW
Air properties: 	ta = 24.00 oC 	RH = 61.21 % 	x = 0.01142 kg.wv/kg.da 	status = UNSATURATED
m_Ma = 1.569 kg/s 	moist air mass flow	 | v_Ma = 1.346 m3/s 	moist air vol flow	 |  v_Ma = 4843.9 m3/h 	moist air vol flow
m_Da = 1.552 kg/s 	dry air mass flow	 | v_Da = 1.306 m3/s 	dry air vol flow	 |  v_Da = 4702.1 m3/h 	dry air vol flow
tx_Out = 24.00 oC 	inlet air temperature
RH_Out = 61.21 % 	inlet air relative humidity
x_Out = 0.01142 kgWv/kgDa 	inlet air humidity ratio

>>CONDENSATE FLOW:
Flow name: 		FlowOfFluid
Locked flow: 	        MASS_FLOW
m_Con = 0.003 kg/s 	condensate mass flow	 | v_Con = 0.000003 m3/s 	condensate vol flow	 |  v_Con = 0.011 m3/h 	condensate vol flow
tx_Con = 11.00 oC 	condensate temperature

>>HEAT OF PROCESS:
Q = -20496.41 W 	 heating / cooling power

>>COIL PROPERTIES:
ts_hydr = 8.00 oC 	 coolant supply temperature
tr_hydr = 14.00 oC 	 coolant return temperature
tm_wall = 11.00 oC 	 average linear wall temperature
BF = 0.619 - 		 coil bypass factor
-----------------------------------------END OF RESULTS-----------------------------------------
```
Based on the results above: the required cooling power for the process is 20,5kW. Airflow properties have changed from 32oC/50% to 24oC/66.8%. Average wall temperature (tm_wall)
and bypass factors were calculated based on the provided input data. <br>

LIMITATIONS:<br>
A typical HVAC temperature range should be used. Achieving negative temperatures for cooling is not possible at the moment, ice buildup is not included in the process.
At this stage of development, this functionality is dedicated to air conditioning, not industrial deep cooling/freezing.
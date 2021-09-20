## DEW|POINT - Psychrometrics Analysis Tool

### Thermodynamics of humid air for MEP engineers.

> AUTHOR: <b>Piotr Jażdżyk</b> <br>
> VERSION: 1.0.0 <br>
> DATE: 2021.09.17<br>
> EMAIL: jazdzyk@gmail.com <br>
> LINKEDIN: https://www.linkedin.com/in/pjazdzyk/ <br>
> COMPANY: SYNERSET / https://www.synerset.com | info@synerset.com

### SHORT DESCRIPTION:
The purpose of this project is to develop a software analysis platform for any kind of psychrometrics analytical calculations dedicated to HVAC and process engineering without using an i-x chart.
At the present development state, only the humid air physics module is complete. 
It allows for determining the temperature and pressure-dependent thermophysical properties of air. To improve code efficiency instead of the "table search interpolation" approach - most of the properties are introduced as an expression (for ie: polynomial fitting) based on available data and scientific papers.  In many cases, available approximations are used as a starting point for more exact results by the use of an improved version of the numerical Brent-Decker solver developed especially for the needs of this application.

All references are listed at the end of this document. Currently, the air physics' module is capable of calculating the following properties:
* atmospheric pressure based on height above sea level,
* temperature change based on height above sea level,
* saturation vapor pressure,
* dew point temperature and wet bulb temperature,
* air relative humidity,
* humidity ratio and maximum humidity ratio,
* kinematic and dynamic viscosity,
* thermal conductivity,
* specific enthalpy of air as well as water mist and ice mist components,
* specific heat,
* density,
* thermal diffusivity,
* prandtl number.

All the properties are independently calculated for dry air, water vapor, water mist, ice mist. Based on these partial properties the final humid air property is determined.
The water physics module is limited only to the properties required for typical psychrometrics calculation:
* specific heat,
* density,
* specific enthalpy.

Each physical property has provided a reference source as well as the argument limit range for which the result is still reliable. There is also a number of method allowing for calculations of some properties if others are available.
All methods are written using the SI units. However, please read method java-doc to make sure that you are aware what unit it outputs. Some properties for an example specific enthalpy,
outputs in kJ/kgK not in J/kgK.

If you would like to participate and help in the development process, feel free to contact me. Together we can do more!

### LEGAL DISCLAIMER:
This code is my intellectual property. Please have respect for this. You can use it freely in any academic or non-commercial use if you
properly include the source and author in your references or documentation. For commercial use, please contact me first.

### CURRENT FUNCTIONALITY:
1. Thermophysical properties of moist air based on ASHRAE HandBook and scientific papers as listed in "References",
2. Limited thermophysical properties of liquid water,
3. Brent-Decker equation solver with automatic counterpart point evaluation algorithm,

### JAVA NAMING CONVENTION DEVIATIONS:
This code is dedicated for read and development to programmers with a strong academic mechanical engineering background. 
To improve code readability for this group of engineers I have decided to deviate from some elements of Java naming convention.
For an example, code for determining humidity ratio is written as below:
```
return x * Pat / ((WG_RATIO * RH / 100.0) + x * RH / 100.0);
```
instead of:
```
return humidityRatio * atmosphericPressure / ((WG_RATIO * relativeHumidity / 100.0) + humidityRatio * relativeHumidity / 100.0);
```
For any MEP engineer the first one is much easier to read, as it is very close to the equation notation in handbooks and guidelines.
Method naming convention is key is as following:
```
calc_Ma_Ta_TdpRH(){}
```
- first fragment - "calc": stands for calculation type method of a given property. "Set" is for setters, "get" for getters. 
- second fragment - next part is a type of fluid: Ma - moist air, Da - dry air, Wv - water vapour, Wt - water
- third fragment - is a name of variable returned by this function, Ta means that this function outputs air temperature as the result,
- fourth part - are the variables needed as an input. 

Approach presented above, which probably may cause a serious headache to any professional programmer, helps me organize all methods into a clear
structure, where corresponding methods are sorting themselves while writing a code using this library.

### UPCOMING FUNCTIONALITIES:
1. Module for volumetric or mass flow handling of any liquid (80%)
2. Module for humid air heating and cooling with a condensate discharge (70%)
3. Module for humid air mixing with multiple streams of air (50%)
4. Module for water spray humidification
5. Module for superheated steam humidification

### IMPROVEMENT REQUEST:
1. Code review and improvement suggestions,
2. Any clever idea how to handle the problem of determining humidity ration if Ps is greater than Pat (for high air temperatures above 90 oC)
3. Any clever idea how to determine humidity ratio if the humid air enthalpy, air temperature and Pat is available (seems easy, but the problem is that for the temperatures close to 0oC, 
for which two different air states can result in the same specific enthalpy).
4. Information on any papers or scientific journals which may contain better accuracy temperature and pressure dependent property formulation.

### USER GUIDE:
At the moment it is very simple. Just create an instance of MoistAir using any of the available constructors:
```
MoistAir air = new MoistAir("air1",20,40, Defaults.DEF_PAT,MoistAir.REL_HUMID);
System.out.println(air);
```
If you change any for the key properties ny use of setters all other dependent properties will be automatically updated.

### REFERENCE SOURCE:
AIR AND WATER VAPOUR PROPERTIES:
* [1] - ASHRAE FUNDAMENTALS 2002, CHAPTER 6 "Psychrometrics"
* [2] - Buck, Arden L. "New Equations for Computing Vapour Pressure and Enhancement Factor". Journal of Applied Meteorology and Climatology (December 1981).
* [3] - Buck Research Instruments L.L.C. "MODEL CR-1A HYGROMETER WITH AUTO FILL OPERATING MANUAL" (May 2012).
* [4] - Morvay Z.K, Gvozdenac D.D. "Fundamentals for analysis and calculation of energy and environmental performance". Applied Industrial Energy And Environmental Management.
* [5] - Lipska B. "Projektowanie Wentylacji i Klimatyzacji. Podstawy uzdatniania powietrza" Wydawnictwo Politechniki Śląskiej (Gliwice  2014).
* [6] - https://www.engineeringtoolbox.com
* [7] - Stull R. "Wet-Bulb Temperature from Relative Humidity and Air Temperature". Manuscript received 14 July 2011, in ﬁnal form 28 August 2011
* [8] - Tsilingiris P.T "Thermophysical and transport properties of humid air at temperature range between 0 and 100oC". Elsevier, Science Direct (September 2007)
* [9] - E.W. Lemmon, R.T. Jacobsen, S.G. Penoncello, D. Friend. Thermodynamic Properties of Air and Mixtures of Nitrogen, Argon, and Oxygen from 60 to 2000 K at Pressures to 2000 MPa. J. Phys. Chem. Ref. Data, Vol. 29, No. 3, (2000)
* [10] - M. Wanielista, R. Kersten,  R. Eaglin. "Hydrology Water Quantity and Quality Control. 2nd ed." (1997).

LIQUID WATER PROPERTIES:
* [1] - F.E. Jones, G.L. Harris. ITS-90 Density of water formulation for volumetric standards calibration. Journal of Research of the National Institute of Standards and Technology (1992)
* [2] - Water specific heat tables: https://www.engineeringtoolbox.com/specific-heat-capacity-water-d_660.html
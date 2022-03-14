## DEW|POINT - Psychrometrics Analysis Tool <br> Thermodynamics of humid air API for MEP/HVAC engineers

> AUTHOR: <b>Piotr Jażdżyk</b> <br>
> VERSION: 1.0.0 <br>
> DATE: 2021.09.17<br>
> LAST UPDATE: 2022.02.23<br>
> EMAIL: jazdzyk@gmail.com<br>
> LINKEDIN: https://www.linkedin.com/in/pjazdzyk/ <br>
> COMPANY: SYNERSET / https://www.synerset.com | info@synerset.com <br>

---

[FUNCTIONALITY](#functionality) | [USER GUIDE](README_GUIDE.MD) | [LEGAL DISCLAIMER](#legal-disclaimer) | [UPCOMING](#upcoming-functionality) | [AUTHORS COMMENTS](#authors-comments) | [RESOURCES](#reference-source)

---

DEW|POINT is analytical psychrometrics tool allowing for moist air properties calculations and most psychrometrics processes in HVAC industry. The aim of this project is to create easy to use psychrometrics API, to be further 
used as an engine for any type of HVAC software development.

At the present state of development following modules are finalized ready for use:
1. Moist Air thermophysical properties (MoistAir) including simplified liquid water properties for condensate handling (LiquidWater)
2. Flow of moist air and liquid water (FlowOfMoistAir)
3. Heating and cooling of moist air, including real cooling coil and its condensate discharge (ProcAirHeatCool)

LAST UPDATE: 14.03.2022 / Added physics of flow, air heating, and cooling.

## FUNCTIONALITY
#### Moist air thermophysical properties:
* atmospheric pressure based on height above sea level,
* temperature change based on height above sea level,
* saturation vapor pressure,
* dew point temperature and wet bulb temperature,
* air relative humidity,
* humidity ratio and maximum humidity ratio,
* kinematic and dynamic viscosity,
* thermal conductivity,
* specific enthalpy of humid air with water mist and ice mist components,
* specific heat,
* density,
* thermal diffusivity,
* Prandtl number.

#### Liquid water properties:
* specific heat,
* density,
* specific enthalpy.

#### Physics of flow:
* each FlowOfMoistAir instance stores moist air volumetric flow, moist air mass flow, dry air volumetric flow, and dry air mass flow.
* each flow type depends on aggregated MoistAir instance and flows are adjusted if the temperature or humidity changes,
* option to lock one selected flow type
* setting value one type of flow will automatically recalculate others, based on provided input value and current properties of the moist air

#### Heating and cooling with condensate discharge:
* moist air heating or dry cooling,
* moist air cooling based on real cooling coil average wall temperature,
* condensate discharge is determined for the cooling process,
* different methods for most typical cases: determining resulting air state based on expected outlet temperature, outlet RH or available heat of the process.

## LEGAL DISCLAIMER
This code is my intellectual property. Please have respect for this. You can use it freely in any academic or non-commercial use if you
properly include the source and author in your references or documentation. For commercial use, please contact me first to discuss the license fee.

## AUTHORS COMMENTS
This code is dedicated to reading and development by programmers with a strong academic mechanical engineering background.
To improve code readability for this group of engineers I have decided to deviate from some elements of the Java naming convention.
For example, the code for determining humidity ratio is written as below:
```
return x * Pat / ((WG_RATIO * RH / 100.0) + x * RH / 100.0);
```
instead of:
```
return humidityRatio * atmosphericPressure / ((WG_RATIO * relativeHumidity / 100.0) + humidityRatio * relativeHumidity / 100.0);
```
For any MEP engineer, the first one is much easier to read, as it is very close to the equation notation in handbooks and guidelines.
Method naming convention is key is as follows:
```
calc_Ma_Ta_TdpRH(){}
```
- a first fragment - "calc": stands for calculation type method of a given property. "Set" is for setters, "get" for getters.
- a second fragment - next part is a type of fluid: Ma - moist air, Da - dry air, Wv - water vapour, Wt - water
- a third fragment - is a name of variable returned by this function, Ta means that this function outputs air temperature as the result,
- a fourth part - are the variables needed as an input.

The approach presented above, which probably may cause a serious headache to any professional programmer, helps me organize all methods into a clear
structure, where corresponding methods are sorting themselves while writing a code using this library.

IMPROVEMENT REQUEST:
1. Code review and improvement suggestions,
2. Any clever idea how to handle the problem of determining humidity ratio if Ps is greater than Pat (for high air temperatures above 90 oC)
3. Any clever idea how to determine humidity ratio if the humid air enthalpy, air temperature, and Pat is available (seems easy, but the problem is that for the temperatures close to 0oC,
   for which two different air states can result in the same specific enthalpy).
4. Information on any papers or scientific journals which may contain better accuracy temperature and pressure-dependent property formulation.

## UPCOMING FUNCTIONALITY
1. Module for humid air mixing with multiple streams of air (50%)
2. Module for water spray air humidification
3. Module for superheated steam air humidification
4. Module for simplified heat and humid recovery exchanger

## REFERENCE SOURCE

Each physical property has provided a reference source as well as the argument limit range for which the result is still reliable. There are also several methods allowing for calculations of some properties if others are available.
All methods accept arguments and output results in SI units. However, please read method java-doc to make sure that you are aware of what unit output value is represented. Some properties for example specific enthalpy,
outputs in kJ/kgK not in J/kgK.

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
* [1] - F.E. Jones, G.L. Harris. ITS-90 Density of water formulation for volumetric standards' calibration. Journal of Research of the National Institute of Standards and Technology (1992)
* [2] - Water specific heat tables: https://www.engineeringtoolbox.com/specific-heat-capacity-water-d_660.html

> <b>*"EARTH MUST COME FIRST"*</b>
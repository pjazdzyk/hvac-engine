
## DEW|POINT - Psychrometrics Analysis Tool

### USER GUIDE

### [MOIST AIR](#MOIST-AIR-THERMOPHYSICAL-PROPERTIES:) | 

Below there is a simple API user guide for a few typical HVAC cases:

### MOIST AIR THERMOPHYSICAL PROPERTIES:
Let's create an object that represent thermophysical properties of air for a typical summer design values: ta=30oC, RH=45%.
```
var summerAir = new MoistAir("summer", 30, 45);
```
By default, all pressure-dependent variable are calculated for atmospheric pressure pat=101325 Pa. It is possible to use overloaded constructor which allows for providing user defined atmospheric pressure and humidity type.
```
var summerAir2 = new MoistAir("summer2", 30, 45, 90000, MoistAir.HumidityType.REL_HUMID);
```
It is also possible to set a specific altitude above sea level, and application will correct pressure value accordingly:
```
summerAir2.setElevationASL(2000);
System.out.println(summerAir2.getPat()); //Outputs: 79495.12 Pa
```
For increased readability Builder Pattern has been introduced. Additionally, a set of static "of" methods are provided for user convenience.
```
var summerAir3 = new MoistAir.Builder()
                             .withName("Summer3")
                             .withTa(30).withRH(45)
                             .withZElev(2000)
                             .build();
                             
var summerAir4 = MoistAir.ofAir(30,45);

```
For quick review of all thermophysical parameters just invoke toString(). Each property is also available by appropriate get() methods.
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
## HVAC|Engine - Psychrometrics Analysis Library.<br> Thermodynamics of Humid Air for MEP/HVAC Engineers.

> Author: **Piotr Jazdzyk**, MScEng<br>
> LinkedIn: https://www.linkedin.com/in/pjazdzyk <br>

---

### [USER GUIDE](README_GUIDE.MD) &nbsp;|&nbsp; [FUNCTIONALITY](#functionality) &nbsp;|&nbsp; [REFERENCES](#reference-sources)

---

HVAC|Engine is a comprehensive library for calculating moist air properties, encompassing crucial thermodynamic processes 
such as heating, dry cooling, real cooling with condensate discharge, mixing single or multiple air streams, and more. 
Our goal is to provide a straightforward and efficient psychrometrics library, ideal for integration into any HVAC software 
development project.<br>

**<span style="color: green;">Thread-Safe Architecture:</span>** This library is developed to ensure thread safety, allowing for concurrent access without 
compromising data integrity through the utilization of immutable objects. The **<span style="color: red;">exception</span>** is modular data flow processing model, 
which components are designed as mutable and stateful. See User Guide, section: 5.

### IMPORTANT NOTICE
Effective from 10th April 2025, the HVAC-Engine library has been divided into two distinct projects.
This repository will continue to serve as the **public version** of the library, which will receive limited maintenance, 
bug fixes, and updates to dependencies. **No new features will be introduced in this version.**<br>

The decision to split the project stems from the evolution of AI-driven scraping technology, which benefits large 
companies and their AI models - at the expense of open-source creators. Given this shift, it became necessary for the
library to transition to a **commercial model**.<br>

The **hvac-engine-pro** project will serve as the **private version**, where all future features and enhancements will
be developed. Access to this version will be available within a paid plan. For more information or to discuss licensing details, 
please feel free to [contact me directly](https://www.linkedin.com/in/pjazdzyk).

### INSTALLATION AND TECH

Copy the Maven dependency provided below to your pom.xml file, and you are ready to go. For other package managers, check maven central repository:
[HVAC|Engine](https://search.maven.org/artifact/com.synerset/hvac-engine/1.1.0/jar?eh=).

```xml
<dependency>
    <groupId>com.synerset</groupId>
    <artifactId>hvac-engine</artifactId>
    <version>2.2.0</version>
</dependency>
```

<strong>HVAC|Engine</strong> is developed using the following technologies: <br>

Core: <br>
![image](https://img.shields.io/badge/21-Java-orange?style=for-the-badge) &nbsp;
![image](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white) &nbsp; ![image](https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white) &nbsp;

Engineering: <br>
[![Unitility](https://img.shields.io/badge/UNITILITY-v2.8.0-13ADF3?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMi41bW0iIGhlaWdodD0iMTQuNW1tIiB2aWV3Qm94PSIwIDAgMjI1MCAxNDUwIj4NCiAgPHBvbHlnb24gZmlsbD0iIzUwN0QxNCIgcG9pbnRzPSIyMjQxLjAzLDE1Ljg4IDExMzYuMzgsMTUuODQgOTA1Ljg4LDQxNS4xIDIwMTAuNTMsNDE1LjA5IiAvPg0KICA8cG9seWdvbiBmaWxsPSIjNzFBQjIzIiBwb2ludHM9IjExMTYuMzgsMTUuODQgNjU1Ljk5LDE1Ljg0IDQ5NC4xNSwyOTYuMTcgNzI4LjM1LDY5NC44OCIgLz4NCiAgPHBvbHlnb24gZmlsbD0iIzhBQzkzNCIgcG9pbnRzPSI0ODQuMTUsMzA2LjE3IDI1NS4wNiw3MDIuOTYgMzg3LjY2LDkzMi42NCA4NDUuODMsOTMyLjYzIiAvPg0KICA8cG9seWdvbiBmaWxsPSIjNThEMEZGIiBwb2ludHM9Ii03LjE3LDE0NDAuMDkgMTA5Ny45NywxNDQwLjA4IDEzMjguNDcsMTA0MC44MyAyMjMuMzIsMTA0MC44NSIgLz4NCiAgPHBvbHlnb24gZmlsbD0iIzEzQURGMyIgcG9pbnRzPSIxNzM5LjA0LDExNjAuOTEgMTUwOS4wOSw3NjIuNjQgMTExNy45NywxNDQwLjA4IDExODYuOTMsMTQ0MC4wOCAxNTc3Ljg3LDE0NDAuMDgiIC8+DQogIDxwb2x5Z29uIGZpbGw9IiMwMzkzRDAiIHBvaW50cz0iMTk3OC44LDc1Mi45NiAxODQ2LjIsNTIzLjMgMTM4Ni42OCw1MjMuMyAxNzQ5LjA0LDExNTAuOTEiIC8+DQo8L3N2Zz4=)](https://github.com/pjazdzyk/Unitility) &nbsp;
[![Brent-Dekker-Solver](https://img.shields.io/badge/Brent_Dekker%20solver-v2.1.0-13ADF3?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMi41bW0iIGhlaWdodD0iMTQuNW1tIiB2aWV3Qm94PSIwIDAgMjI1MCAxNDUwIj4NCiAgPHBvbHlnb24gZmlsbD0iIzUwN0QxNCIgcG9pbnRzPSIyMjQxLjAzLDE1Ljg4IDExMzYuMzgsMTUuODQgOTA1Ljg4LDQxNS4xIDIwMTAuNTMsNDE1LjA5IiAvPg0KICA8cG9seWdvbiBmaWxsPSIjNzFBQjIzIiBwb2ludHM9IjExMTYuMzgsMTUuODQgNjU1Ljk5LDE1Ljg0IDQ5NC4xNSwyOTYuMTcgNzI4LjM1LDY5NC44OCIgLz4NCiAgPHBvbHlnb24gZmlsbD0iIzhBQzkzNCIgcG9pbnRzPSI0ODQuMTUsMzA2LjE3IDI1NS4wNiw3MDIuOTYgMzg3LjY2LDkzMi42NCA4NDUuODMsOTMyLjYzIiAvPg0KICA8cG9seWdvbiBmaWxsPSIjNThEMEZGIiBwb2ludHM9Ii03LjE3LDE0NDAuMDkgMTA5Ny45NywxNDQwLjA4IDEzMjguNDcsMTA0MC44MyAyMjMuMzIsMTA0MC44NSIgLz4NCiAgPHBvbHlnb24gZmlsbD0iIzEzQURGMyIgcG9pbnRzPSIxNzM5LjA0LDExNjAuOTEgMTUwOS4wOSw3NjIuNjQgMTExNy45NywxNDQwLjA4IDExODYuOTMsMTQ0MC4wOCAxNTc3Ljg3LDE0NDAuMDgiIC8+DQogIDxwb2x5Z29uIGZpbGw9IiMwMzkzRDAiIHBvaW50cz0iMTk3OC44LDc1Mi45NiAxODQ2LjIsNTIzLjMgMTM4Ni42OCw1MjMuMyAxNzQ5LjA0LDExNTAuOTEiIC8+DQo8L3N2Zz4=)](https://github.com/pjazdzyk/brent-dekker-solver) &nbsp;

CI/CD:<br>
![image](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white) &nbsp;
![image](https://img.shields.io/badge/Sonar%20cloud-F3702A?style=for-the-badge&logo=sonarcloud&logoColor=white) &nbsp;

## FUNCTIONALITY

#### Moist air properties:

* atmospheric pressure based on height above sea level,
* temperature change based on height above sea level,
* water vapour saturation pressure,
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
* mass flow,
* volumetric flow,
* mass flow of dry air component,
* volumetric flow of dry air component,
* based on flow type specified during instance creation, other flows will be recalculated accordingly for specified
  Fluid properties,

#### Air heating:
* heating process for input heat,
* heating process for target outlet air temperature,
* heating process for target outlet air relative humidity,

#### Air cooling:
* dry cooling process for input heat,
* dry cooling process for target outlet air temperature,
* real cooling process with condensate discharge process for input heat,
* real cooling process with condensate discharge process for target outlet temperature,
* real cooling process with condensate discharge process for target outlet relative humidity,

#### Air stream mixing:
* simple mixing of two flows with humidity content,
* mixing of multiple flows with humidity content,

## License
GNU AGPL 3.0 LICENSE.<br>
This work is licensed under the terms of the GNU Affero General Public License (AGPL) 3.0. You may copy, modify, 
and distribute this work, provided that you comply with the terms of the AGPL. If you modify and distribute the work, 
you must provide proper attribution to the original author, [Piotr Jazdzyk](https://www.linkedin.com/in/pjazdzyk), 
in all derivative works and publications. Additionally, if you use the software over a network, you must make the 
source code available to the users of that network.

## Acknowledgments
I extend my heartfelt gratitude to the [Silesian University of Technology](https://www.polsl.pl/en/) for imparting invaluable knowledge to me.<br>
Badges used in readme: [Shields.io](https://img.shields.io) and [Badges 4 README.md](https://github.com/alexandresanlim/Badges4-README.md-Profile).

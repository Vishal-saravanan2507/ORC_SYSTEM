# Industrial Waste Heat to ORC Electricity Generation and Monitoring System

A full-stack college project that simulates and monitors electricity generation
from industrial waste heat using an **Organic Rankine Cycle (ORC)**.

> ⚠️ **This is a software simulation and monitoring tool, not a certified
> industrial engineering design system.** All power, energy, and CO2 figures
> are simplified engineering **estimates**, clearly labelled as such
> throughout the application, and are configurable rather than hard-coded.

---

## 1. Problem Statement

Many industrial processes (steel furnaces, cement kilns, glass furnaces, boilers,
gas turbines, etc.) reject large amounts of thermal energy as waste heat.
Instead of losing this heat, an Organic Rankine Cycle (ORC) system can recover
a portion of it and convert it into electricity — improving overall plant
efficiency and reducing reliance on grid power (and its associated emissions).

## 2. Objective

Give an industrial operator a simple system to:
1. Register industrial units.
2. Enter waste-heat operating parameters.
3. Automatically calculate recoverable heat, ORC electrical power, daily/
   monthly/annual energy, and avoided CO2 emissions.
4. Store historical measurements in MySQL.
5. Visualize performance through a dashboard and monitoring charts.
6. Generate printable performance reports.

## 3. Features

| Module | What it does |
|---|---|
| **Industrial Unit Management** | Full CRUD, search by name/location, filter by status |
| **Waste Heat Data** | Enter measurements; recoverable heat/power/energy/CO2 are calculated automatically on save |
| **ORC Calculation Engine** | Dedicated `ORCCalculationService`; stateless what-if calculator page + used by every save |
| **Performance Monitoring** | Historical table + trend charts, filterable by unit/source/date range |
| **Dashboard** | KPI cards + 4 Chart.js charts, all computed from live database data |
| **Reports** | Aggregate stats over a date range for one or all units, with browser print support |

## 4. Technology Stack

- **Backend:** Java 17, Spring Boot 3.3, Spring Web, Spring Data JPA (Hibernate), Spring Validation, Maven
- **Database:** MySQL 8+
- **Frontend:** HTML5, CSS3, vanilla JavaScript, Chart.js (via CDN)
- **Testing:** JUnit 5, Spring Boot Test, MockMvc, H2 (in-memory, test-only)

No React/Angular/Vue/Node/Python/Django/MongoDB is used anywhere.

## 5. System Architecture

```
Browser (HTML/CSS/JS, served from Spring Boot static/ folder)
        │  REST calls (fetch) — JSON over HTTP
        ▼
Spring Boot REST Controllers
        │
Service Layer (business rules, ORC calculation engine)
        │
Spring Data JPA Repositories
        │
MySQL Database (orc_waste_heat_db)
```

The frontend is served directly from `src/main/resources/static/`, so the
whole application (frontend + backend) runs from a single Spring Boot process
at **http://localhost:8080** — no separate frontend server is required.

## 6. Database Design

```
IndustrialUnit (1) ───< (many) WasteHeatData (1) ─── (1) ORCPerformance
```

**industrial_units**: id, industry_name, industry_type, location,
contact_person, installed_capacity, created_date, status

**waste_heat_data**: id, industrial_unit_id (FK), heat_source, flow_rate,
inlet_temperature, outlet_temperature, specific_heat, heat_recovery_factor,
operating_hours, operating_days, recorded_date

**orc_performance**: id, waste_heat_data_id (FK, unique), recoverable_heat_kw,
orc_efficiency, electrical_power_kw, daily_energy_kwh, monthly_energy_kwh,
annual_energy_kwh, daily_co2_reduction_kg, monthly_co2_reduction_tonnes,
annual_co2_reduction_tonnes, calculated_at

Tables are created/updated automatically by Hibernate (`ddl-auto=update`) —
no manual SQL scripts are required.

## 7. Calculation Methodology

All calculations live in `ORCCalculationService.java`. Simplified formulas:

```
ΔT (K)              = Inlet Temp (°C) − Outlet Temp (°C)
Total Heat Q (kW)   = Mass Flow Rate (kg/s) × Specific Heat (kJ/kg·K) × ΔT
Recoverable Heat    = Q × Heat Recovery Factor (%)
Electrical Power    = Recoverable Heat × ORC Efficiency (%)
Daily Energy (kWh)  = Electrical Power × Operating Hours
Monthly Energy      = Daily Energy × Operating Days
Annual Energy       = Monthly Energy × 12
Daily CO2 (kg)      = Daily Energy × Grid Emission Factor (kg CO2/kWh)
Monthly CO2 (t)     = (Monthly Energy × Grid Emission Factor) / 1000
Annual CO2 (t)      = (Annual Energy × Grid Emission Factor) / 1000
```

**Verified example** (used as the JUnit test fixture):

```
m = 10 kg/s, Cp = 1.1 kJ/kg·K, Tin = 450°C, Tout = 200°C
ΔT = 250 K
Q  = 10 × 1.1 × 250        = 2750 kW
Recoverable Heat (70%)     = 2750 × 0.70   = 1925 kW
Electrical Power (18%)     = 1925 × 0.18   = 346.5 kW
Daily Energy (24 h)        = 346.5 × 24    = 8316 kWh
Monthly Energy (30 days)   = 8316 × 30     = 249,480 kWh
```

## 8. Installation & Setup

### Prerequisites
- JDK 17+
- Maven 3.8+ (or use your IDE's bundled Maven)
- MySQL Server 8+, running locally
- Internet access on the machine you build this on (Maven needs to download
  dependencies the first time, and Chart.js is loaded from a CDN)

### 8.1 MySQL Setup
```sql
CREATE DATABASE orc_waste_heat_db;
```
(The app's connection string also includes `createDatabaseIfNotExist=true`,
so this step is optional but recommended.)

### 8.2 Configure the connection
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```
Replace `YOUR_PASSWORD` with your actual MySQL root password (or a dedicated
user's credentials).

### 8.3 Build and run the backend
```bash
mvn clean install
mvn spring-boot:run
```
On first startup, Hibernate creates the tables and `DataInitializer` seeds 3
sample industries with 5 sample measurements (only if the database is empty).

### 8.4 Open the application
Because the frontend is served from Spring Boot's `static/` folder, just open:

```
http://localhost:8080
```

Log in with the demo credentials below.

## 9. Test Credentials (Demo Login Only)

```
Username: admin
Password: admin123
```
This is a **frontend-only demo login** using `localStorage` as a session flag.
It is **not** production security — there is no Spring Security layer, and
anyone with direct API access can still call the REST endpoints. This is
intentional and documented, per the project's "beginner-friendly, no
unnecessary complexity" scope. See section 14 for what a production version
would need.

## 10. API Endpoints

**Industrial Units**
```
GET    /api/industrial-units              (optional ?search=, ?status=ACTIVE|INACTIVE)
GET    /api/industrial-units/{id}
POST   /api/industrial-units
PUT    /api/industrial-units/{id}
DELETE /api/industrial-units/{id}
```

**Waste Heat Data**
```
GET    /api/waste-heat                    (optional ?industrialUnitId=, ?heatSource=, ?startDate=, ?endDate=)
GET    /api/waste-heat/{id}
POST   /api/waste-heat
PUT    /api/waste-heat/{id}
DELETE /api/waste-heat/{id}
```

**ORC Calculator (stateless)**
```
POST   /api/orc/calculate
```

**Dashboard**
```
GET    /api/dashboard
```

**Reports**
```
GET    /api/reports                       (optional ?industrialUnitId=, ?startDate=, ?endDate=)
```

### Sample requests (for Postman)

`POST /api/industrial-units`
```json
{
  "industryName": "XYZ Textile Mill",
  "industryType": "TEXTILE",
  "location": "Coimbatore, Tamil Nadu",
  "contactPerson": "K. Raman",
  "installedCapacity": 1500,
  "status": "ACTIVE"
}
```

`POST /api/waste-heat`
```json
{
  "industrialUnitId": 1,
  "heatSource": "FURNACE",
  "flowRate": 10,
  "inletTemperature": 450,
  "outletTemperature": 200,
  "specificHeat": 1.1,
  "heatRecoveryFactor": 70,
  "orcEfficiency": 18,
  "operatingHours": 24,
  "operatingDays": 30,
  "gridEmissionFactor": 0.7
}
```

`POST /api/orc/calculate`
```json
{
  "flowRate": 10,
  "inletTemperature": 450,
  "outletTemperature": 200,
  "specificHeat": 1.1,
  "heatRecoveryFactor": 70,
  "orcEfficiency": 18,
  "operatingHours": 24,
  "operatingDays": 30,
  "gridEmissionFactor": 0.7
}
```
Expected response:
```json
{
  "recoverableHeatKw": 1925.0,
  "electricalPowerKw": 346.5,
  "dailyEnergyKwh": 8316.0,
  "monthlyEnergyKwh": 249480.0,
  "annualEnergyKwh": 2993760.0,
  "dailyCO2ReductionKg": 5821.2,
  "monthlyCO2ReductionTonnes": 174.636,
  "annualCO2ReductionTonnes": 2095.632
}
```

`GET /api/dashboard` and `GET /api/reports` take no body — just call them
with GET after some waste-heat data exists.

## 11. Running the Tests

```bash
mvn clean test
```
Tests use an in-memory H2 database (`src/test/resources/application.properties`)
so they do not require MySQL to be running. Covered:
- Recoverable heat calculation
- ORC electrical power calculation
- Daily / monthly / annual energy calculation
- CO2 reduction calculation
- Invalid temperature input (inlet ≤ outlet)
- Invalid efficiency input (out of 0–100 range)
- Invalid flow rate / heat recovery factor
- End-to-end `/api/orc/calculate` endpoint test via MockMvc

## 12. Project Structure

```
orc-project/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/orc/wasteheat/
    │   │   ├── WasteHeatOrcApplication.java
    │   │   ├── controller/    (5 REST controllers)
    │   │   ├── service/       (5 services incl. ORCCalculationService)
    │   │   ├── repository/    (3 Spring Data JPA repositories)
    │   │   ├── entity/        (IndustrialUnit, WasteHeatData, ORCPerformance)
    │   │   ├── dto/           (request/response DTOs)
    │   │   ├── exception/     (ResourceNotFoundException, GlobalExceptionHandler)
    │   │   └── config/        (CorsConfig, DataInitializer)
    │   └── resources/
    │       ├── application.properties
    │       └── static/        (the entire frontend)
    │           ├── index.html, dashboard.html, industrial-units.html,
    │           │   waste-heat.html, orc-calculator.html, monitoring.html,
    │           │   reports.html
    │           ├── css/ (style.css, dashboard.css, forms.css)
    │           └── js/  (api.js, dashboard.js, industrial-units.js,
    │                     waste-heat.js, orc-calculator.js, monitoring.js,
    │                     reports.js)
    └── test/
        ├── java/com/orc/wasteheat/service/ORCCalculationServiceTest.java
        ├── java/com/orc/wasteheat/controller/ORCControllerTest.java
        └── resources/application.properties  (H2 test config)
```

## 13. Known Limitations

- The ORC model is a simplified estimation (single-stage energy balance),
  not a full thermodynamic cycle simulation (no working-fluid selection,
  pinch-point analysis, or component-level modelling).
- Authentication is a demo-only frontend gate, not real security.
- No pagination on list endpoints — fine for a college dataset size, not for
  large-scale production data.
- Dates use the browser's local date; no timezone handling beyond that.
- CO2 reduction is an "avoided emissions" estimate based on a user-supplied
  grid emission factor, not an audited figure.

## 14. Future Enhancements

- Real IoT sensor integration (MQTT) for live temperature/flow data
- SCADA integration
- Advanced thermodynamic modelling (working-fluid selection, heat exchanger
  sizing, pinch analysis)
- Machine-learning-based efficiency prediction / predictive maintenance
- Cloud deployment
- Real Spring Security with role-based authentication
- Real industrial data integration

## 15. Final Quality Checklist

- [x] Spring Boot application structure complete and internally consistent
- [x] MySQL datasource configured (`YOUR_PASSWORD` placeholder, not hard-coded)
- [x] JPA entities create the 3 required tables with correct relationships
- [x] Sample data loads only when the database is empty
- [x] Industrial unit CRUD implemented (controller → service → repository)
- [x] Waste heat data CRUD implemented, auto-triggers ORC calculation
- [x] ORC calculation engine isolated in its own service, unit-tested
- [x] Validation implemented (bean validation + cross-field business rules)
- [x] Dashboard, monitoring, and reports all read from the database, not
      hard-coded values
- [x] Charts (Chart.js) render from `/api/dashboard` data
- [x] Filtering implemented via backend query parameters
- [x] Print report via `window.print()`, no paid PDF library
- [x] Frontend calls REST APIs (no broken URLs, no hard-coded results)
- [x] JUnit tests written for the calculation engine and the calculate endpoint

> **Note on this delivery:** the code was written and reviewed carefully,
> and the calculation logic was hand-verified against the worked example
> above, but it was **not compiled or run in the environment that produced
> it** (no Maven/MySQL/internet access there). Please run `mvn clean test`
> and `mvn spring-boot:run` on your own machine as the final verification
> step, and report back any compiler errors you hit so they can be fixed.

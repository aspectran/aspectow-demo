# Aspectow Demo Site: Comprehensive WAS Reference Application

The Aspectow Demo Site is a comprehensive reference application designed to showcase the multi-application integration, enterprise architecture, and operational capabilities of the **Aspectow Web Application Server (WAS)** powered by the [Aspectran](https://aspectran.com/) framework.

It demonstrates how enterprise web applications, RESTful services, real-time interactive components, and monitoring agents run concurrently within a unified Aspectow runtime environment.

<img width="1042" alt="aspectow-demo-screenshot" src="https://cdn.jsdelivr.net/gh/aspectran/aspectran.github.io@main/images/projects/aspectow-demo.png">

## Hosted Web Applications

The demo site hosts distinct, fully functioning web applications served simultaneously by the embedded Undertow server:

* **Main Portal (`/`)**
  * Central entry point and landing dashboard linking to all hosted applications and demonstration modules.

* **JPetStore Demo (`/jpetstore/`)**
  * A full-featured enterprise e-commerce reference application ported to Aspectran.
  * Demonstrates MyBatis integration, declarative transaction management, shopping cart session handling, and order workflows.

* **PetClinic Demo (`/petclinic/`)**
  * The classic PetClinic enterprise application built with Aspectran.
  * Demonstrates JPA / Hibernate ORM integration, Querydsl type-safe querying, bean validation, and Thymeleaf layout templating.

* **Aspectran Examples & Skylark Showcase (`/demo/`)**
  * **WebSocket Chat:** Real-time multi-user bidirectional chat service with custom JSON encoders/decoders.
  * **Text-to-Speech (TTS):** Server-side audio synthesis and streaming powered by FreeTTS.
  * **File Upload:** Async multipart file upload and processing.
  * **RESTful Services & Translet Interpreter:** Dynamic Translet execution and interactive web terminal.

* **Aspectow AppMon (`/console/`)**
  * Embedded Application Monitoring engine exposing real-time server health, JVM heap and Undertow worker thread pool metrics, live request activities, active sessions, and log tailing with WHOIS IP geolocation resolution.

* **H2 Database Console (`/h2console/`)**
  * Integrated web-based H2 database management console for inspecting application data schemas and tables.

## Architecture & Integration

`aspectow-demo` operates as an enterprise WAS node capable of coordinating with the centralized [Aspectow Demo Console](https://github.com/aspectran/aspectow-demo-console) via Redis.

```text
┌─────────────────────────────────────────────────────────────┐
│                 Aspectow Demo Site (WAS)                    │
│                                                             │
│  ┌──────────────┐ ┌──────────────┐ ┌─────────────────────┐  │
│  │ Main Portal  │ │  JPetStore   │ │      PetClinic      │  │
│  │     (/)      │ │ (/jpetstore) │ │    (/petclinic)     │  │
│  └──────────────┘ └──────────────┘ └─────────────────────┘  │
│  ┌───────────────────────────────┐ ┌─────────────────────┐  │
│  │   Aspectran Examples (/demo)  │ │ AppMon Engine Agent │  │
│  │  - WebSocket Chat, TTS, etc.  │ │     (/console)      │  │
│  └───────────────────────────────┘ └─────────────────────┘  │
│  ─────────────────────────────────────────────────────────  │
│             Embedded Undertow Server Engine (:8080)         │
└──────────────────────────────┬──────────────────────────────┘
                               │ (Cluster & Metrics Bus)
                               ▼
                          ┌─────────┐
                          │  Redis  │
                          └─────────┘
```

## Key Technical Features

* **Unified Multi-Context Hosting:** Runs multiple independent web applications and REST APIs concurrently within a single embedded Undertow server without separate container deployments.
* **Diverse Persistence Stack:** Integrates MyBatis (for SQL-centric data mapping) and JPA/Hibernate + Querydsl (for domain-driven ORM) in harmony.
* **Modern Templating:** Full support for Thymeleaf (with Layout Dialect) and JSP view resolvers.
* **Real-Time Communication:** Low-latency bidirectional WebSocket communication with custom message pipelines.
* **Session Management & Clustering:** Flexible session backends supporting local file stores as well as distributed Redis (Lettuce) clustering.
* **Observability & Health Monitoring:** Built-in AppMon exporters for live JVM memory, thread pool statistics, access logs, and request tracing.

## Project Structure

```text
aspectow-demo/
├── app/
│   ├── bin/             Runtime scripts for Shell, Daemon, and Windows Service (Procrun)
│   ├── config/          Application rules, server configurations, AppMon, and logging setups
│   ├── lib/             Runtime dependencies and application JARs (generated on build)
│   ├── logs/            Application, access, and subsystem logs
│   └── webapps/         Static web resources, templates, and WEB-INF assets
│       ├── demo/        Aspectran Examples & WebSocket chat assets
│       ├── jpetstore/   JPetStore web application
│       ├── petclinic/   PetClinic web application
│       └── root/        Main portal landing page
├── setup/               Server deployment scripts and service installation utilities
├── src/                 Java source code (PetClinic, JPetStore, Chat, TTS, AppMon)
└── pom.xml              Maven build configuration
```

## Configuration & Profiles

Configuration files are located under `app/config/`:

* `aspectran-config.apon`: Core Aspectran configuration, shell commands, and daemon settings
* `server/server.xml`: Undertow server and servlet context routing configurations
* `console/node-config.apon`: Node and cluster settings
* `console/appmon-config.apon`: AppMon monitoring targets, metrics, and log settings
* `console/redis-dev.properties` / `redis-prod.properties`: Redis connection settings

### Active Profiles

* `dev` (default): Configured for development environment.
* `prod`: Configured for production environment with Redis session clustering.
* `appmon.ext-persistence`: Enables external database persistence for AppMon monitoring data.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE.txt](LICENSE.txt) file for details.

# Backend – Dialysis Record System

Este repositorio contiene la implementación backend básica de un sistema para registrar, gestionar y consultar el historial diario de diálisis peritoneal de un paciente.  
El objetivo es ofrecer una **API REST mantenible, escalable y bien estructurada**, que sirva como base para futuros módulos como análisis clínicos, reportes, alertas o integración con aplicaciones móviles.


## **Descripción general**

El sistema modela tres entidades principales:

- **Doctor**
- **Patient**
- **Session** (registro de una sesión de diálisis)

Incluye operaciones CRUD completas, permite asignar pacientes a doctores y registra sesiones clínicas ordenadas por fecha.  
La API expone endpoints REST consistentes y organizados bajo una arquitectura clara orientada a la mantenibilidad y extensibilidad.


## **Arquitectura y metodología**

El backend está desarrollado con Spring Boot siguiendo el patrón **MVC**, organizado en las siguientes capas:

### **Model**
Entidades JPA (Doctor, Patient, Session), con relaciones **OneToMany** y **ManyToOne** gestionadas mediante Hibernate.

### **Repository**
Interfaces basadas en **JpaRepository**, responsables del acceso a datos y consultas derivadas.

### **Service**
Capa de lógica de negocio estructurada en interfaces e implementaciones.  
Incluye validaciones, operaciones sobre entidades y uso de **DTOs y mappers**.

### **Controller**
Exposición de endpoints HTTP mediante `RestController` y `ResponseEntity`.  
Define la interfaz pública de la API.

### **DTOs y Mappers**
Capa destinada a desacoplar el modelo de persistencia de la API, facilitando claridad, seguridad y futuras modificaciones.


## **Tecnologías utilizadas**

- **Java 24**
- **Spring Boot** (Web, JPA, Validation)
- **Hibernate**
- **Spring Data JPA**
- **Lombok**
- **Mappers** (manuales o MapStruct)
- **PostgreSQL**


## **Funcionalidades principales**

- CRUD completo para **Doctor**, **Patient** y **Session**
- Registro de sesiones de diálisis ordenadas por fecha
- Asignación y desasignación de pacientes a un doctor
- Uso de **DTOs y mappers**
- Soft delete opcional mediante anotaciones de Hibernate
- Validaciones básicas
- Arquitectura modular orientada a la escalabilidad


## **Estado del proyecto**

Versión base funcional que puede actuar como backend independiente o como API para una aplicación web o móvil.

Próximas mejoras previstas:

- Autenticación y autorización (JWT)
- Gestión de roles (doctor/paciente)
- Validaciones avanzadas
- Paginación y filtrado
- Manejo global de excepciones
- Versionado de la API
- Pruebas unitarias e integración


## **Objetivo final**

El proyecto busca evolucionar hacia un sistema completo de apoyo para pacientes y profesionales, permitiendo registrar de forma estructurada el tratamiento diario de diálisis peritoneal y generando análisis clínicos para mejorar el seguimiento.  
Esta implementación inicial sienta las **bases arquitectónicas** para el crecimiento futuro del sistema.

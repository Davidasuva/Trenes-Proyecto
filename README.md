# **Sistema de Gestión de Trenes de Transporte de Pasajeros**

Sistema desarrollado para la organización de transporte UPB, orientado a la automatización del control de carga, descarga y gestión integral de trenes de pasajeros.

## **Descripción General**

El Sistema de Gestión de Trenes de Transporte de Pasajeros, automatiza los procesos que actualmente se realizan de forma manual en la operación del servicio de transporte. Este sistema cubre desde la compra de boletos hasta la entrega de equipaje, así como labores administrativas.

## **Problematica**

Al desarrollarse toda la gestión de manera manual, esto genera tiempos tardios al momento de la ejecución de los servicios. Además, genera cierto chance de error humano en la ejecución. Por eso se generl la necesidad de crear este sistema de gestión.

## **Funcionalidades Principales**

### **Gestión de Pasajeros**
- Compra de boletos en máquinas de la estación.
- Revisión y validación del boleto para abordaje.
- Control de equipaje.
- Validación del boleto durante el trayecto.
- Desembarco y entrega de quipaje.

### **Gestión Administrativa**
- Alta y baja de trenes.
- Creación y administración de rutas.
- Publicación de rutas disponibles
- Recomendación de rutas.
- Gestión de empleados y pasajeros.
- Publicación del orden de abordaje en monitores de la estación.

## **Información contenida en los boletos**
- Id de registro.
- Fecha y hora de compra.
- Fecha y hora de salida y llegada.
- Datos básicos del pasajero. (También Vagón y asiento del pasajero).
- Datos del tren. (Tipo de tren).
- Datos de la ruta elegida ( Destino, Inicio).
- Categoria del boleto (Premium / Ejecutiva / Estándar).
- Valor del pasaje.
- Información del equipaje ( Id, Peso, Vagón de carga).

## **Rutas y horarios**
- Las rutas son configuradas con información de tren, origen, destino y estaciones intermedias.
- Los horarios pueden ser modificados siempre y cuando el trayecto no haya iniciado.
- El valor del pasaje se ajusta según los kilómetros recorridos y la categoria del boleto.
- El sistema recomienda la ruta más corta al pasajero.
- Se admiten rutas con transbordo entre trenes para llegar al destino.

## **Módulo de Empleados**
- Consulta y modifica datos de trenes y rutas.
- Administra horarios y recorridos.

## **Módulo de Abordaje**
Al momento del abordaje, el sistema publicara en los monitores de la estación el orden de abordaje de atrás hacia adelante, respetando la siguiente prioridad:
1- Premium.
2- Ejecutivo.
3- Estándar.

# **Diseño del Sistema**
## **Diagrama de Clases**
<img width="2211" height="1838" alt="Clases" src="https://github.com/user-attachments/assets/b3f593b6-4fb6-4479-a98f-02b8efa3a34b" />

## **Diagrama de Componentes**
<img width="2669" height="1746" alt="ComponentesArreglado vpd" src="https://github.com/user-attachments/assets/f43a359b-d412-4379-8edc-6998347ea1c5" />

# **Notas técnicas**
- Pueden haber cambios de diseño durante el desarrollo del proyecto, esto débido a la falta de experiencia por parte de las partes involucradas dentro del proyecto. 

# **Como inicializar el programa**
Escribe mvn javafx:run en la terminal, iniciaria el programa
# **Integrantes del Equipo de Desarrollo**
- [David Santiago Peña González](https://github.com/Davidasuva)
- [Juan Sebastian Rueda Velandia](https://github.com/juanse1172)

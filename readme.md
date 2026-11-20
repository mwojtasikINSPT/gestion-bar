# 🍹 GestiónBar

Sistema de gestión para bares desarrollado en **Java**, aplicando programación orientada a objetos, arquitectura MVC y principios SOLID.

## 📋 Descripción

**GestiónBar** es una aplicación que permite administrar las operaciones de un bar, incluyendo la gestión de clientes, meseros, mesas, reservas y asignaciones.

El proyecto está diseñado con una arquitectura modular que facilita el mantenimiento, la reutilización y la ampliación del código.

## 🛠️ Tecnologías utilizadas

- ☕ **Java**
- 🧩 **Programación Orientada a Objetos (POO)**
- 🏗️ **Arquitectura MVC** (Modelo-Vista-Controlador)
- 💾 **Patrón DAO** (Data Access Object)
- 📐 **Principios SOLID**
- 📁 **Persistencia mediante archivos TXT**

## ⚙️ Funcionalidades

- 👥 **Clientes:** gestión de información de clientes.
- 🧑‍🍳 **Meseros:** administración del personal de atención.
- 🪑 **Mesas:** gestión de mesas del establecimiento.
- 📅 **Reservas:** registro y administración de reservas.
- 🔄 **Asignaciones:** asignación de mesas y meseros.
- 💾 **Persistencia:** almacenamiento y recuperación de información mediante archivos de texto.

## 🏛️ Arquitectura

El proyecto implementa el patrón **MVC**, separando las responsabilidades en tres componentes:

- 📦 **Modelo:** representa las entidades y reglas de negocio.
- 🖥️ **Vista:** permite la interacción con el usuario.
- 🎮 **Controlador:** coordina la comunicación entre la vista y la lógica del sistema.

Además, utiliza el patrón **DAO** para abstraer el acceso a los datos y mantener la persistencia independiente de la lógica de negocio.

## 🧠 Principios de diseño

El sistema aplica los siguientes conceptos:

- 🔒 **Encapsulamiento:** protección del estado interno de los objetos.
- 🧩 **Abstracción:** uso de interfaces para definir comportamientos.
- 🔀 **Polimorfismo:** implementación de diferentes comportamientos mediante contratos comunes.
- 📐 **SOLID:** separación de responsabilidades y reducción del acoplamiento.

## 💾 Persistencia de datos

La información se almacena en archivos de texto (`.txt`), utilizando implementaciones **DAO** para realizar operaciones de lectura y escritura.

Este enfoque permite conservar los datos entre ejecuciones sin depender de un motor de base de datos.

## 🎯 Objetivo

Desarrollar un sistema de gestión que permita aplicar los conceptos fundamentales de la **programación orientada a objetos**, los **patrones de diseño** y las **buenas prácticas de arquitectura de software**.

## 📌 Estado del proyecto

🎓 Proyecto académico desarrollado con fines educativos.
# Proyecto-SIUP

Programa en Java que gestiona personas (estudiantes, docentes y externos), pagos, comprobantes y reembolsos, siguiendo un diagrama UML. Toda la interacción se hace por consola.

## Estructura del proyecto
 
```
Proyecto-SIUP/
├── source/        # código fuente (.java)
├── out/        # clases compiladas (se genera al compilar)
└── README.md
```

## Cómo ejecutar
 
1. Clona el repositorio y entra a la carpeta:
```bash
git clone https://github.com/Jhosue0Aguilar/Proyecto-SIUP
cd Proyecto-SIUP
```
 
2. Compila el código:
```bash
mkdir -p out
javac -d out source/*.java
```
 
3. Ejecuta el programa:
```bash
java -cp out Main
```
 
En Windows, si `mkdir -p` no funciona, crea la carpeta `out` a mano y usa los otros dos comandos igual.

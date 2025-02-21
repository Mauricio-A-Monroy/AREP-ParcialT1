# Parcial Teórico AREP T1
## Arquitectura y funcionalidad

![image](https://github.com/user-attachments/assets/c8885dbf-aa7a-4275-82ce-fbe82766cba8)

La solucion a este parcial consta de implementar 2 servidores http y un cliente HTML y JS:
- El servidor que funciona como backEnd recibe unicamente peticiones que tenga la siguiente estructura _/compreflex?comando=_ seguido de uno de los 4 comandos que soporta:
  - _Class([class name])_: Retorna una lista de campos declarados y métodos declarados.
  - _invoke([class name],[method name])_: retorna el resultado de la invocación del método.
  - _unaryInvoke([class name],[method name],[paramtype],[param value])_: retorna el resultado de la invocación del método.
  - _binaryInvoke([class name],[method name],[paramtype 1],[param value], [paramtype 1],[param value],)_: retorna el resultado de la invocación del método.
  
  Un ejemplo de como hacer una petición al backEnd es http://localhost:8080/compreflex?comando=binaryInvoke(java.lang.Math,max,double,4.5,double,-3.7)
- El servidor que funciona como fachada recibe 2 peticiones que tienen la siguiente estructura:
  - _/cliente_ : Este servicio entrega el cliente web en formato html + js.
  - _/consulta?comando=[comando con parámetros separados por coma entre paréntesis, solo soporta los 4 comando mencionados anteriormente]_ : retorna el valor solicitado en formato String.

  Un ejemplo de como hacer una petición al servidor fachada es http://localhost:8080/consulta?comando=binaryInvoke(java.lang.Math,max,double,4.5,double,-3.7)
- Cliente HTML y JS es un archivo .HTML secillo que contiene un espacio para que el usuario agrege el comando que quiera ejecutar, ademas de un boton para hacer la petición al servidor backEnd, la respuesta del backEnd se verá abajo del voton en formato String.

¡ACLARACIÓN IMPORTANTE! los parámetros que se envían en los comando de invoke, unaryInvoke y binaryInvoke solo se separan por una coma, no se debe agragar ningun espacio entre ellos, ademas, los 2 servidores (backEnd y fachada) deben estár ejecutándose al tiempo.

## Ejecución

Comando para copilar el proyecto:
    
    mvn pacakge

Comando para ejecutar el servidor fachada:
    
    java -cp "target/classes" edu.escuelaing.arep.parcial.FacadeServer

Comando para ejecutar el servidor backEnd:
    
    java -cp "target/classes" edu.escuelaing.arep.parcial.BackEndServer
  

## Pruebas de usuario

Las pruebas a continuación serán hechas únicamente en el cliente HTML y JS, ya que esté debe encargarse hacer la petición al servidor fachada, y este a su vez de modificar el path y enviárselo al servidor backEnd para obtener una respuesta, así que se están probando los 3 componentes de la arquitectura a la vez.
- Comando usado: Class(java.lang.System)

  ![image](https://github.com/user-attachments/assets/1c4c7469-a13f-4921-969e-d75c1ecae3d8)


- Comando usado: invoke(java.lang.System, getenv)

  ![image](https://github.com/user-attachments/assets/be80becb-5f40-4480-b581-1354dc6d519f)

- Comando usado: unaryInvoke(java.lang.Integer,valueOf,String,"34233")

  ![image](https://github.com/user-attachments/assets/0d0d4124-c158-4514-aaaf-c00ac6c46702)

- Comando usado: binaryInvoke(java.lang.Math,max,double,4.35,double,4.37)

  ![image](https://github.com/user-attachments/assets/e1ad1421-365f-4488-86ef-0b0c31dd8c17)

  


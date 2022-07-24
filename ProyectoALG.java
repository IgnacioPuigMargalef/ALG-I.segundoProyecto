/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto.alg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nacho && Jesus
 */
public class ProyectoALG {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String directorio = "data"; //Directiorio en el que tenemos las instancias: dado que esta dentro de la carpeta del proyecto, 
        //esta ruta sera valida independientemente del ordenador en el que estemos.
        String[] files = getFiles(directorio);

        for (int i = 0; i < files.length; i++) {
            float[][] matrizCoordenadasCiudades = leerFichero(files[i]);
            float[][] matrizDistancia = obtenerMatrizDistancia(matrizCoordenadasCiudades);
            System.out.println("Estamos en el fichero:" + (i + 1));
            aleatorioCP1(matrizDistancia, 100);
            aleatorioCP1(matrizDistancia, 500);
            aleatorioCP1(matrizDistancia, 1000);
            aleatorioCP2(matrizDistancia, 25);
            aleatorioCP2(matrizDistancia, 50);
            aleatorioCP2(matrizDistancia, 100);
            voraz(matrizDistancia);
            System.out.println("");
        }

    }

    /**
     * Con este metodo obtendremos cada uno de los ficheros en formato string
     *
     * @param dir string con el nombre del directorio en el que tenemos las
     * instancias
     * @return los un array de string con el nombre de cada uno de los ficheros.
     * Null en caso de pasar un directorio no valido
     */
    public static String[] getFiles(String dir) {
        File f = new File(dir); //Instanciamos f, que sera un objeto de tipo file que contendra el directorio en el que se encuentran nuestras instancias
        if (f.isDirectory()) {  //Comprobamos que es un directorio valido con un metodo de la clase file
            List<String> lista = new ArrayList(); //Creamos una lista
            File[] listaFicheros = f.listFiles(); //Creamos un array de ficheros de tipo file

            int size = listaFicheros.length;
            for (int i = 0; i < size; i++) { //Meteremos los ficheros de tipo file en una lista
                if (listaFicheros[i].isFile()) {
                    lista.add(listaFicheros[i].toString());
                }
            }
            String[] ficheros = lista.toArray(new String[0]); //Los sacamos como string para poder acceder a ellos posteriormente
            return ficheros;
        } else {
            System.out.println("Directorio no valido");
        }
        return null;
    }

    /**
     * Este metodo va a leer el fichero que le pasemos por parametro como string
     *
     * @param nombreFichero
     * @return
     */
    public static float[][] leerFichero(String nombreFichero) {

        //Declarar una variable BufferedReader
        BufferedReader br = null;
        try {
            //Crear un objeto BufferedReader al que se le pasa 
            //   un objeto FileReader con el nombre del fichero
            br = new BufferedReader(new FileReader(nombreFichero));
            //Leer la primera línea, guardando en un String
            String texto = br.readLine();
            //

            //Sacamos el numero de ciudades de la instancias que estamos leyendo
            int numeroCiudades = sacarNumeroCiudades(nombreFichero);
            float[][] matrizCiudades = new float[numeroCiudades][3]; //Nuestra matriz de ciudades contendra tantas filas como ciudades haya, y las columnas correspondaran a:
            // numero ciudad - coordenada x - coordenada y

            while (!texto.equalsIgnoreCase("NODE_COORD_SECTION")) {  //Nos situaremos en esta linea para poder proceder a leer la primera ciudad 
                texto = br.readLine();
            }
            // 

            int i = 0;
            while (i < numeroCiudades) {
                texto = br.readLine();
                matrizCiudades = obtenerMatrizCiudades(matrizCiudades, texto, i); //Leemos la linea y llamamos a obtenerMatrizCiudades, que nos devolvera 
                // una matriz con la linea leida y convertida del fichero a numero 
                i++;
            }
            return matrizCiudades;
        } catch (FileNotFoundException e) {
            System.out.println("Error: Fichero no encontrado");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de lectura del fichero");
            System.out.println(e.getMessage());
        } finally { //finalmente, cerraremos el fichero. Tendreamos el cuenta que podemos obtener errores al cerrar el fichero, por lo que controlaremos excepciones
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar el fichero");
                System.out.println(e.getMessage());
            }
        }

        //System.out.println(nombreFicheros.length);
        return null;
    }

    public static float[][] obtenerMatrizCiudades(float[][] m, String texto, int fila) {
        //  Ejemplo: 1 572 340
        //Saco el numero de la ciudad del texto que le pasamos por parametro y lo meto en la primera columna de la matriz
        int i = PosSinEspacio(texto);

        String StringSinEspacioComienzo = texto.substring(i); //Comenzando desde el primer caracter del numero de la ciudad en el que estemos
        //creamos un substring con los espacios quitados del principio
        //Ejemplo:   1 23 323 --> :1 23 323
        int indice = StringSinEspacioComienzo.indexOf(" ");   //Donde esta el siguiente espacio? Esto determinara hasta donde leer el numero
        float num = Float.parseFloat(StringSinEspacioComienzo.substring(0, indice)); //Creamos un substring con el numero de ciudad en el que estemos y parseamos para poder tenerlo en formato numerico

        m[fila][0] = num; // Dado que es el numero de ciudad en el que estamos, lo guardamos en la primera columna

        //Saco la coordenada x del texto que le pasamos por parametro y lo introducimos en la columna 1 de la matriz
        //en la fila que le pasamos por parametro, que correspondera con la fila de la ciudad
        //Haremos algo parecido a lo anterior pero ahora para sacar la coordenada x
        StringSinEspacioComienzo = StringSinEspacioComienzo.substring(indice + 1);

        i = PosSinEspacio(StringSinEspacioComienzo);
        String textoCoordenadas = StringSinEspacioComienzo.substring(i);

        indice = textoCoordenadas.indexOf(" ");

        num = Float.parseFloat(textoCoordenadas.substring(0, indice));

        m[fila][1] = num;

        //Saco la coordenada y del texto que le pasamos por parametro y lo introducimos en la columna 1 de la matriz
        //en la fila que le pasamos por parametro, que correspondera con la fila de la ciudad
        //Haremos algo parecido a lo anterior pero ahora para sacar la coordenada x
        textoCoordenadas = textoCoordenadas.substring(indice);

        i = PosSinEspacio(textoCoordenadas);

        String textoCoordenadaY = textoCoordenadas.substring(i);
        num = Float.parseFloat(textoCoordenadaY);
        m[fila][2] = num;

        return m; // devolvemos una matriz con la fila parseada a numero y con los numeros metidos en sus respectivas columnas

    }

    /**
     * Con este metodo sacaremos en que posicion empezamos a leer el numero de
     * ciudad en el que nos encontramos Esto lo realizamos dado que en algunos
     * casos sera: 1 En otros casos sera : 23 En otros :182 Incluso con
     * magnitudes mayores
     *
     * @param texto linea del fichero en la que estemos situados
     * @return posicion en la que empezamos a leer la ciudad
     */
    public static int PosSinEspacio(String texto) {
        int i = 0;
        char letra = texto.charAt(i);//
        boolean enc = false;
        while (!enc) {
            if (letra != ' ') {
                enc = true;
            } else {
                i++;
                letra = texto.charAt(i);
            }
        }
        return i;
    }

    /**
     * Este metodo lo usaremos para saber cuantas ciudades existen en la
     * instancia Sera importante a la hora de crear la matriz con la informacion
     * del numero de ciudad, coordenada x & coordenada y Cada fichero tendra
     * diferentes numero de ciudades, por lo que a la hora de instanciar la
     * matriz, querremos saber que tamanyo darle a las filas
     *
     * @param fichero
     * @return
     * @throws IOException
     */
    public static int sacarNumeroCiudades(String fichero) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fichero));
        String texto = br.readLine();

        while (!texto.substring(0, 9).equalsIgnoreCase("DIMENSION")) { //Nos situamos en la linea 9, que contiene una cadena de texto tal que asi: "DIMENSION: 280"
            texto = br.readLine();
        }
        int i = 0;
        while (texto.charAt(i) != ':') { //Sacamos la posicion de la cadena en la que tenemos los dos puntos, ya que nos interesa leer lo proximo: el numero de ciudades
            i++;
        }
        String dimensionSinDosPuntos = texto.substring(i + 1); //Creamos un substring comenzando en el siguiente caracter despues de los dos puntos

        i = PosSinEspacio(dimensionSinDosPuntos); //Obtenemos desde donde empezar a leer el numero (string)

        String digitosDimension = dimensionSinDosPuntos.substring(i); //Creamos un substring con el numero

        int tam = Integer.parseInt(digitosDimension); //Casteamos y lo obtenemos en formato int

        return tam;

    }

    /**
     * A partir de lo realizado anteriormente, sacaremos la matriz de distancia
     * de la instancia en la que nos encontremos en nuestro intento de
     * ahorrarnos tiempo de ejecución, tuvimos en cuenta que no iba a ser
     * necesario recorrer las nxn posiciones de la matriz: recorrimos solo los
     * elementos que se situaban por encima de la diagonal principal (m[i][j]),
     * y poniendo la misma distancia en la posición pero esta vez por debajo
     * (m[j][i]). La diagonal principal está llena de ceros.
     *
     * @param matrizfichero
     * @return
     */
    public static float[][] obtenerMatrizDistancia(float[][] matrizfichero) {
        float[][] matrizDistancia = new float[matrizfichero.length][matrizfichero.length];
        for (int i = 0; i < matrizfichero.length; i++) {
            float x1 = matrizfichero[i][1]; //coordenada x
            float y1 = matrizfichero[i][2]; //coordenada y
            for (int j = i; j < matrizfichero.length; j++) {
                if (i != j) {
                    float x2 = matrizfichero[j][1];
                    float y2 = matrizfichero[j][2];
                    float distanciaEuclidea = distEuclidea(x1, x2, y1, y2); //distancia entre la ciudad actual y todas las demas de la instancia
                    matrizDistancia[i][j] = distanciaEuclidea;
                    matrizDistancia[j][i] = distanciaEuclidea;
                } else {
                    matrizDistancia[i][j] = 0;
                }
            }
        }
        return matrizDistancia;
    }

    /**
     * Distancia euclidea entre dos ciudades
     *
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     * @return
     */
    public static float distEuclidea(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     *
     * @param m Esta matriz representa la amtriz de distancia
     * @param numIteraciones esta variable representa el numero de iteraciones
     * que se realizaran para buscar el mejor camino sera 100,500 y 100
     */
    public static void aleatorioCP1(float[][] m, int numIteraciones) { //Este es el aleatorio para el criterio de parada 1 que actualiza la mejor solucion y por tanto se queda con el mejor camino de los 100,500 o 1000 generados
        int[] camino = new int[m.length + 1]; //inicializamos el camino a la longitud de la matriz de distancia + 1 ya que tiene que acabar en la misma ciudad por la que empieza,
        //por tanto el camino tendra una ciudad mas de las que posee la matriz de distancia 
        float distanciaTotal = -1;

        for (int i = 0; i < numIteraciones; i++) {  //Bluce que busca un camino aleatorio tantas veces como se ele indique 

            for (int j = 0; j < camino.length; j++) {//Este bucle pone el camino al completo a cero para que la primera y ultima ciudad sean la cero 
                camino[j] = 0;
            }

            for (int j = 1; j < camino.length - 1; j++) { //Este bucle se encaga de llamar tantas veces como sea la longuitud del camino 
                //a la funcion que nos devuelve una ciudad aleatoriamente  la cual no esta repetida en el camino
                camino[j] = getValorAleatorioNoEnCamino(m.length - 1, camino);//Se añade la ciudad generada aleatoriamente al camino
            }

            float distancia = getDistanciaTotal(m, camino);//obtenemos la distancia del camino

            if (distancia < distanciaTotal || distanciaTotal == -1) { //Aqui actualizamos la distanciaTotal a la nueva distancia si esta ultima era mejor que la distanciaTotal
                distanciaTotal = distancia;

            }
        }
        System.out.println("Aleatoriao Criterio Parada1 con " + numIteraciones + " iteraciones : " + distanciaTotal);

    }

    public static void aleatorioCP2(float[][] m, int valorMax) {
        int[] camino = new int[m.length + 1];
        float distanciaTotal = -1;
        int cont = 0;

        while (cont <= valorMax) {
            for (int j = 0; j < camino.length; j++) {
                camino[j] = 0;
            }

            for (int j = 1; j < camino.length - 1; j++) {
                camino[j] = getValorAleatorioNoEnCamino(m.length - 1, camino);
            }
            float distancia = getDistanciaTotal(m, camino);

            if (distancia < distanciaTotal || distanciaTotal == -1) {
                cont = 0;
                distanciaTotal = distancia;
            } else {
                cont++;
            }
        }
        System.out.println("Aleatoriao Criterio Parada2 con contador con valor  " + valorMax + "  : " + distanciaTotal);

    }

    /**
     *
     * @param distancia matriz de distancia
     * @param camino camino al cual hay que calcular la distancia
     * @return distanciaTotal
     */
    public static float getDistanciaTotal(float[][] distancia, int[] camino) {//Funcion que nos devuelve la distancioa total del camino que se le pasa como parametro 

        float distanciaTotal = 0;
        for (int i = 0; i < camino.length - 1; i++) { //Este bucle se encarga de sacar la distancia que hay entre todas las ciudades del camino
            int origen = camino[i];                   //Tenemos la ciudad de origen de donde partimos y la de destino que sera la siguiente posicion del camino 
            int destino = camino[i + 1];
            distanciaTotal += distancia[origen][destino]; //En la mattriz de distancia colocamos en las filas el origen y las columnas el destino,obtendremos la distancia de origen a destino,
            //y se le aniade a distanmcia total
        }

        return distanciaTotal;                         //Variable que al final del bucle contendra la distancia total del camino
    }

    /**
     *
     * @param valorMaximo valor maximo de ciudades que poseemos
     * @param noUsar vector con el camino y por tanto que contiene las ciudades
     * utilizadas y que no pueden ser usadas
     * @return
     */
    public static int getValorAleatorioNoEnCamino(float valorMaximo, int[] noUsar) { //Funcion que nos devuelve un valor aleatorio que no se encuentra en el camino 
        //entre el rango de numero de ciudades que poseemos 
        int generado = -1;
        boolean enc = true; //

        while (enc) { //Bucle que hasta no encontrar una ciudad que no se encuentre en el camino no para de generar numeros aleatorios corresponidentes al nunero de la ciudad 
            generado = (int) (Math.random() * valorMaximo + 1); //Genera un numero entre 0 y el valor maximo correspondiente al numero maximo de ciudad
            enc = false;
            int i = 0;

            while (i < noUsar.length && !enc) { // Bucle que comprueba que el numero generado correspondiente a una ciudad  no se encuente en el camino 
                if (generado == noUsar[i]) {
                    enc = true;
                }
                i++;
            }
        }

        return generado; //ciudad generada que no se encuentra en el camino y se va a proceder a añadir

    }

    /**
     *
     * @param matrizDistancias matriz de distancias
     * @return distanciaTotal del camino
     */
    public static float voraz(float[][] matrizDistancias) {
        int[] caminoCiudades = new int[matrizDistancias.length + 1];//Inicializamos el camino a la longitud de la matriz de distancia + 1 ya que tiene que acabar en la misma ciudad por la que empieza,
        //por tanto el camino tendra una ciudad mas de las que posee la matriz de distancia 
        caminoCiudades[0] = 0; //Ponemos a cero la primera y ultima ciudad del camino para que empiecen y acaben en la misma ciudad
        caminoCiudades[matrizDistancias.length] = 0;

        for (int i = 1; i < caminoCiudades.length - 1; i++) {//Bucle que completara el camino
            caminoCiudades[i] = obtenerCiudadMasCercana(caminoCiudades[i - 1], caminoCiudades, matrizDistancias, i);//funcion que nos devuelve la  ciudad  mas cercana a la que estamos
            //sin repetir ciudades
        }
        float distanciaTotal = getDistanciaTotal(matrizDistancias, caminoCiudades);//obtenemos la distancia del camino
        System.out.println("Voraz " + distanciaTotal);

        return distanciaTotal;
    }

    /**
     *
     * @param ciudadOrigen ciudad desde la que partimos para buscar la que se
     * encuentre mas cerca
     * @param ciudadesVisitadas ciudades visitadas corresponde al camino
     * @param matrizDistancias
     * @param numeroCiudadesVisitadas el numero de ciudades que posee el camino
     * @return
     */
    public static int obtenerCiudadMasCercana(int ciudadOrigen, int[] ciudadesVisitadas, float[][] matrizDistancias, int numeroCiudadesVisitadas) {//Metodo que obtiene una ciudad 
        //que no se encuentra en el camino y es la mas cercana a la ciudad que se le pasa como parametro "ciudadOrigen"

        int numCiudad = 1;//variable para recorrer las ciudades 
        int ciudadMejorDistancia = 0; //variable que contendra la ciudad con mejor distancia a la ciudad de origen
        float mejorDistancia = Integer.MAX_VALUE; //variable  que contendra la mejor distancia 
        float distanciaActual;
        while (numCiudad < matrizDistancias.length) {//Bucle para recorrer todas las ciudades y obtener la que tiene mejor distancia desde la ciudad de origen 

            if (!isVisitado(numCiudad, ciudadesVisitadas, numeroCiudadesVisitadas) && numCiudad != ciudadOrigen) { //si la ciudad esta visitada o es la misma que la de de origen no realizamos las operaciones 
                distanciaActual = matrizDistancias[ciudadOrigen][numCiudad]; //obtenemos la distancia de la ciudad de origen a la que indique numCiudad
                if (distanciaActual < mejorDistancia) { //si la distancia obtenida es menor que mejor distancia tendremos una nueva ciudad que no esta repetida y con mejor distancia 
                    mejorDistancia = distanciaActual;
                    ciudadMejorDistancia = numCiudad;
                }
            }
            numCiudad++;

        }
        return ciudadMejorDistancia;
    }

    /**
     *
     * @param numCiudad la ciudad la cual queremos averiguar si se encuentra en
     * el camino
     * @param ciudadesVisitadas camino que contiene las ciudades que lo componen
     * y por tanto visitadas
     * @param numeroCiudadesVisitadas numero de ciudades que posee el camino
     * @return
     */
    public static boolean isVisitado(int numCiudad, int[] ciudadesVisitadas, int numeroCiudadesVisitadas) {
        int i = 1;
        while (i < numeroCiudadesVisitadas - 1) { // Bucle para buscar si la ciudad se encuentra en  el camino 
            if (numCiudad == ciudadesVisitadas[i]) {
                return true;
            }
            i++;
        }
        return false;
    }
}

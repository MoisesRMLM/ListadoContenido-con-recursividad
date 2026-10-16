// Importamos Path y Files del paquete java.nio.file.
//
// Path representa una ruta de un fichero o directorio.
// Files contiene métodos para trabajar con esas rutas.
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.AccessDeniedException;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.Date;
import java.util.Scanner;

public class listadoContenido {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Sacamos el path de la carpeta del usuario
        Path dir = Path.of(System.getProperty("user.home"));

        System.out.println("Ubicación: " + dir.normalize());

        System.out.println("Indica la profundidad a la que quieres llegar con la búsqueda:");
        int prof = sc.nextInt();
        try {
            listado(dir, prof, 0); // prof:1 -> solo muestra la carpeta usuario, 2 -> muestra los contenidos de la carpeta usuario, 3 -> muestra los contenidos de esas carpetas, ...
        } catch (IOException e) {
            System.err.println("Error: " + e);
        } // try-catch
    } // main

    static void listado(Path rutaBase, int profundidad, int sangria) throws IOException{
        // Comprobar que la ruta no está oculta
        if (!Files.isHidden(rutaBase) && profundidad > 0) {
            for (int i = 0; i < sangria; i++) {
                System.out.print("  ");
            } // for

            // Imprime el nombre del archivo
            System.out.print(rutaBase.getFileName());

            // Archivo normal
            if (Files.isRegularFile(rutaBase)) {
                // Imprime la fecha de la última modificación
                Date fecha = new Date(Files.getLastModifiedTime(rutaBase).toMillis());
                System.out.print(" | Fecha: " + fecha);

                // Imprime el propietario
                System.out.print(" | Propietario: " + Files.getOwner(rutaBase));

                // Peso del archivo
                System.out.print(" | Tamaño: " + ajustarTamanio(Files.size(rutaBase)));

                System.out.println();
            // Carpeta
            } else if (Files.isDirectory(rutaBase)) {
                System.out.println();
                
                // Utiliza recursividad y se vuelve a llamar a la función listado por cada archivo que contiene
                try (Stream<Path> ficheros = Files.list(rutaBase)) {
                    ficheros.forEach(f -> {
                        try {
                            listado(f, (profundidad - 1), (sangria + 1));
                        } catch (AccessDeniedException e) {
                            // Para que no escriba el mensaje de error "AccessDenied" 1000 veces, el catch está vacio para que no imprima nada
                        } catch (IOException e) {
                            System.err.println("Error: " + e);
                        } // try-catch
                    });
                } // try
            } // if
        } // if
    } // listado

    // Para ajustar las unidades, se intenta dividir entre 1000 hasta que el resultado fuese menor que 1
    // Por cada división que se ha podido realizar, se pasa la  unidad a una superior, hasta llegar a los gb
    static String ajustarTamanio(double tamanio) {
        int unidad = 0; // 0: B, 1: kB, 2: MB, 3: GB

        while (tamanio / 1000 >= 1 && unidad < 3) {
            tamanio /= 1000;
            unidad ++;
        } // while

        switch (unidad) {
            case 0: return tamanio + "B";
            case 1: return tamanio + "kB";
            case 2: return tamanio + "MB";
            case 3: return tamanio + "GB";
            default: return "Error al calcular el tamaño"; // Nunca se llega a este caso, pero es necesario para que java compile
        } // switch
    } // ajustarTamanio
} // listadoContenido
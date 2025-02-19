import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.util.Scanner;

public class FTPCliente {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FTPClient ftpClient = new FTPClient();

        String servidor = "192.168.0.31";
        int puerto = 21;
        String usuario = "";
        String contrasenia = "";
        boolean autenticado = false;

        try {
            ftpClient.connect(servidor, puerto);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            System.out.println("Seleccione el modo de acceso:");
            System.out.println("1. Iniciar sesión con usuario y contraseña");
            System.out.println("2. Acceder como usuario anónimo");
            System.out.print("Opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();

            if (opcion == 1) {
                System.out.print("Usuario: ");
                usuario = scanner.nextLine();
                System.out.print("Contraseña: ");
                contrasenia = scanner.nextLine();

                if (ftpClient.login(usuario, contrasenia)) {
                    System.out.println("Sesión iniciada correctamente.");
                    autenticado = true;
                } else {
                    System.out.println("Error: Credenciales incorrectas.");
                    return;
                }
            } else if (opcion == 2) {
                if (ftpClient.login("anonymous", "")) {
                    System.out.println("Sesión anónima iniciada correctamente.");
                } else {
                    System.out.println("Error: No se pudo acceder en modo anónimo.");
                    return;
                }
            } else {
                System.out.println("Opción inválida.");
                return;
            }

            int opcion2;
            do {
                System.out.println("\nSeleccione una opción:");
                System.out.println("1. Ver lista de archivos");
                System.out.println("2. Descargar un archivo");
                if (autenticado) {
                    System.out.println("3. Subir un archivo");
                }
                System.out.println("4. Salir");
                System.out.print("Opción: ");
                opcion2 = scanner.nextInt();
                scanner.nextLine();

                switch (opcion2) {
                    case 1:
                        listarArchivos(ftpClient);
                        break;
                    case 2:
                        System.out.print("Nombre del archivo a descargar: ");
                        String archivoDescargar = scanner.nextLine();
                        descargaArchivo(ftpClient, archivoDescargar);
                        break;
                    case 3:
                        if (autenticado) {
                            System.out.print("Ruta del archivo a subir: ");
                            String archivoSubir = scanner.nextLine();
                            subidaArchivo(ftpClient, archivoSubir);
                        } else {
                            System.out.println("Opción no válida en modo anónimo.");
                        }
                        break;
                    case 4:
                        System.out.println("Cerrando sesión...");
                        break;
                    default:
                        System.out.println("Opción inválida, intenta nuevamente.");
                }
            } while (opcion2 != 4);

            ftpClient.logout();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        scanner.close();
    }

    private static void listarArchivos(FTPClient ftpClient) throws IOException {
        System.out.println("\nArchivos en el servidor:");
        String[] files = ftpClient.listNames();
        if (files != null) {
            for (String file : files) {
                System.out.println(file);
            }
        } else {
            System.out.println("No se pudo obtener la lista de archivos.");
        }
    }

    private static void descargaArchivo(FTPClient ftpClient, String archivoServidor) throws IOException {
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

        BufferedOutputStream out= new BufferedOutputStream(new FileOutputStream(archivoServidor)  );
        if(ftpClient.retrieveFile(archivoServidor,out)) {
            System.out.println("El archivo se ha descargado correctamente");
        } else{
            System.out.println("No se ha podido descargar el archivo");
        }
        out.close();
    }

    private static void subidaArchivo(FTPClient ftpClient, String archivoSubida) throws IOException {
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        BufferedInputStream in=new BufferedInputStream(new FileInputStream(archivoSubida));

        ftpClient.storeFile(archivoSubida,in);
    }
}

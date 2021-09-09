package cliente;

import RMI.RemoteInterface;
import log.Log;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Cliente extends Canvas implements Serializable{

    public Cliente (String ip, int port, String particiones) {
        try{
            Socket s = new Socket (ip, port);

            // Establecemos los canales de entrada / salida
            BufferedReader in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter    out = new PrintWriter(s.getOutputStream(),true);

            // Enviamos la cantidad de particiones que selecciona en Menu
            out.println(particiones);

            // Cerramos socket
            s.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args){

        try {

            File file = new File("./src/main/resources/image3.jpg");
            BufferedImage image = ImageIO.read(file);
            Scanner entrada = new Scanner(System.in);
            Cliente cliente;
            String opcion = "";
            boolean terminar = false;
            String particiones = "0";
            long startTime = 0;
            while(!terminar) {
                mostrarMenu();
                opcion = entrada.nextLine();

                switch (opcion) {
                        case "1":
                            particiones = "1";
                            cliente = new Cliente("localhost", 8000, particiones);
                            startTime = System.currentTimeMillis();
                            ejecutar(image, particiones);
                            break;
                    case "2":
                            particiones = "2"; // width = 435
                            cliente = new Cliente("localhost", 8000, particiones);
                        startTime = System.currentTimeMillis();

                        ejecutar(image, particiones);
                            break;
                    case "3":
                            particiones = "4"; // width = 217,5
                            cliente = new Cliente("localhost", 8000, particiones);
                        startTime = System.currentTimeMillis();

                        ejecutar(image, particiones);
                            break;
                    case "4":
                            particiones = "8"; // width = 108,75
                            cliente = new Cliente("localhost", 8000, particiones);
                        startTime = System.currentTimeMillis();

                        ejecutar(image, particiones);
                            break;
                    case "5":
                            particiones = "16"; // width = 54,375
                            cliente = new Cliente("localhost", 8000, particiones);
                        startTime = System.currentTimeMillis();

                        ejecutar(image, particiones);
                            break;
                    case "6":
                        particiones = "64"; // width = 54,375
                        cliente = new Cliente("localhost", 8000, particiones);
                        startTime = System.currentTimeMillis();

                        ejecutar(image, particiones);
                        break;
                    case "0":
                            terminar = true;
                            break;
                    default: System.out.println("Opcion incorrecta");
                            break;
                }

                long endTime = System.currentTimeMillis()-startTime;
                if(opcion!="0")
                    System.out.println("El procesamiento en : "+particiones +" particiones, demoro: " + endTime+" ms");
            }

        } catch (Exception e) {
            System.out.println("Error : "+e.getMessage());
            e.printStackTrace();
        }
    }


    public static void ejecutar (BufferedImage image, String particiones) throws IOException {

        HashMap<Integer, byte[]> imagenArray = new HashMap<Integer, byte[]>();

        BufferedImage bImageFromConvert = null;
        byte[] imageInByte = null;

        int partes = Integer.parseInt(particiones);
        int portInicial = 9001;

        // establecemos el ancho de cada sub-imagen
        int width = image.getWidth()/partes;
        int height = image.getHeight();

        // punto desde donde va a comenzar a procesar la imagen
        int punto_x = 0;

        System.out.println("Llama a ejecutar con " + partes + " partes. ");

        for(int i=0; i<partes; i++) {
            // obtenemos la imagen cortada
            BufferedImage image1 = cropImage(image, punto_x, width, height);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(image1, "PNG", bytes);

            Registry registro = LocateRegistry.getRegistry("127.0.0.1", portInicial);
            RemoteInterface ri = null;

            try {
                ri = (RemoteInterface) registro.lookup("Sobel");
            } catch (NotBoundException e) {
                e.printStackTrace();
            }

            System.out.println("Procese " + i + " parte");
            System.out.println("---");
            // Incrementamos variables
            portInicial = portInicial + 1;
            punto_x = punto_x + width;

            // Aplicamos Sobel a la porción de imagen
            imageInByte = ri.getConversion(bytes.toByteArray(), i);

            // Almacenamos parte procesada para luego convertir a imagen
            imagenArray.put(i, imageInByte);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Convertir array de byte enviado por el servicio a imagen nuevamente
        for(int i=0; i<partes; i++) {
            outputStream.write (imagenArray.get(i));
            byte imagenByteFinal[] = outputStream.toByteArray();
            InputStream inFinal = new ByteArrayInputStream(imagenByteFinal);
            bImageFromConvert = ImageIO.read(inFinal);

            System.out.println("log temporal");
            try{
                File output = new File("./src/main/java/cliente/sobel_salida_final"+i+".png");
                ImageIO.write(bImageFromConvert, "png", output);
            }catch(IIOException e){
                System.out.println("Ha ocurrido un error : " + e.getMessage());
            }

            outputStream.reset();
        }

        // Unificamos resultado
        unificar(image, partes);
    }

    /*
    Cortar las imagenes para aplicar Sobel por partes
     */
    private static BufferedImage cropImage(BufferedImage src, int initX, int width, int height) {
        BufferedImage imgCortada = src.getSubimage(initX, 0, width, height);
        return imgCortada;
    }


    /*
    Unificar partes y borrar las partes
     */
    private static void unificar (BufferedImage image, int cantidad) {
        ArrayList<BufferedImage> arrayBI = new ArrayList<BufferedImage>();
        int width = image.getWidth()/cantidad;
        for(int i=0; i<cantidad; i++){
            try {
                arrayBI.add( ImageIO.read(new File("./src/main/java/cliente/sobel_salida_final"+i+".png")) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedImage biResultado = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = biResultado.getGraphics();

        int x = 0;
        for(int i=0; i<cantidad; i++){
           g.drawImage(arrayBI.get(i), x,0, null);
           x = x + width;
        }

        try {
            ImageIO.write(biResultado, "PNG", new File("./src/main/java/cliente/resultado.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Menu de opciones
     */
    public static void mostrarMenu() {
        System.out.println("\n\t---- Menu ----");
            System.out.println("\n1. Procesar imagen completa");
            System.out.println("2. Procesar en 2 partes");
            System.out.println("3. Procesar en 4 partes");
            System.out.println("4. Procesar en 8 partes");
            System.out.println("5. Procesar en 16 partes");
            System.out.println("6. Procesar en 64 partes");
            System.out.println("0. Salir");
            System.out.print("\t Opcion: ");
    }

}

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Servidor {

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Servidor arrancado Que comienze el chat....");
        Datos data = new Datos();

        while(true){
            socket = serverSocket.accept();
            HiloConnect worker = new HiloConnect(data, socket);
            worker.start();
        }
    }

    static class HiloConnect extends Thread{

        private final Datos buffer;
        private Socket socket = null;
        private ObjectInputStream ois = null;
        private ObjectOutputStream oos = null;

        public HiloConnect(Datos data,Socket socket){
            this.buffer = data;
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Conexión recibida desde => " + socket.getInetAddress());

            try {
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());

                String nombreUsuario = (String) ois.readObject();
                System.out.println("Usuario conectado " + nombreUsuario);
                oos.writeObject("Hola " + nombreUsuario + " Bienvenido ");
                oos.writeObject(buffer.obteneMensajes());

                System.out.println("mensajes guardados enviado al cliente .....");

                String texto = "";

                while (!texto.equals("bye")) {
                    texto = (String) ois.readObject();
                    if (texto.equals("bye")) {
                        oos.writeObject("Good bye");
                        System.out.println("me despido del usuario => " + nombreUsuario);
                    } else if (texto.contains("message:")) {

                        DateTimeFormatter diayhora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        String tiempoactual = diayhora.format(LocalDateTime.now());
                        texto = texto.substring(8);
                        texto = texto.substring(0, 0) + tiempoactual + " " + nombreUsuario + " => " + texto + "***";
                        buffer.guardarMensaje(texto);
                        buffer.verMensajes();
                        oos.writeObject(buffer.obteneMensajes());
                    }
                }

            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (oos != null) oos.close();
                    if (ois != null) ois.close();
                    if (socket != null) socket.close();
                    System.out.println("Apagando Servidor !!!! ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

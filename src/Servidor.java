import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Servidor {

    public static void main(String[] args) throws IOException {

        Socket soket = null;
        ServerSocket serversoket = new ServerSocket(8080);
        System.out.println("Servidor arrancado Que comienze el chat....");
        while(true){
            soket = serversoket.accept();
            HiloConnect worker = new HiloConnect(soket);
            worker.start();
        }
    }

    static class HiloConnect extends Thread{

        private Socket soket = null;
        private ObjectInputStream ois = null;
        private ObjectOutputStream oos = null;

        public HiloConnect(Socket soket){
            this.soket = soket;
        }


        @Override
        public void run() {

            System.out.println("Conexión recivida desde => " + soket.getInetAddress());

            try{
                ois = new ObjectInputStream(soket.getInputStream());
                oos = new ObjectOutputStream(soket.getOutputStream());

                String nombreUsuario = (String) ois.readObject();
                System.out.println("Usuario conectado " + nombreUsuario);

                oos.writeObject("Hola " + nombreUsuario + " Bienvenido ");
                System.out.println("Mensaje enviado al cliente .....");
                String valor = "";
                String texto = "";

                while ( !texto.equals("Hola") ){
                    valor = (String) ois.readObject();
                    switch (valor) {
                        case "message:":

                            break;
                        case "bye":
                            oos.writeObject("Good bye");
                            texto = "Hola";
                            System.out.println("Despedida al cliente .....");
                            break;

                        default:
                            oos.writeObject("Texto erroneo... Introduce (message:) o (bye)");
                            break;
                    }
                }



            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (oos != null) oos.close();
                    if (ois != null) ois.close();
                    if (soket != null) soket.close();
                    System.out.println("Se acabó lo que se daba !!!! ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

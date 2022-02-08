import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Datos {

    public Datos() {
        this.mensajes = Collections.synchronizedList(new ArrayList());
    }

    private List<String> mensajes;

    public synchronized void guardarMensaje(String mensaje) throws InterruptedException {
        mensajes.add(mensaje);
    }


    public synchronized String obteneMensajes() throws InterruptedException {
        String textoConcate = "";
        for (int i = 0; i < mensajes.size(); i++) {
           textoConcate += mensajes.get(i);
        }
        return textoConcate;
    }

    public void verMensajes(){
        for (int i = 0; i < mensajes.size(); i++) {
            System.out.println(mensajes.get(i));
        }
    }

}

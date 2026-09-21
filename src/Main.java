import javax.swing.SwingUtilities;
import vista.VentanaCine;

public class Main {
    public static void main(String[] args){
            // Toda la interfaz gráfica debe ejecutarse en el hilo de eventos de Swing.
            SwingUtilities.invokeLater(() -> {
                VentanaCine ventana = new VentanaCine();
                ventana.setVisible(true);
            });

    }

}
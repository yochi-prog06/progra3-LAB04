package vista;

import modelo.FuncionPelicula;
import modelo.ProductoDulceria;
import modelo.TipoFormato;
import modelo.TipoProducto;
import servicio.CineServicio;

import javax.swing.*;
import java.util.Optional;

public class VentanaCine extends JFrame {
    private JPanel panelRaiz;
    private JPanel panelEncabezado;
    private JLabel lblTitulo;
    private JTabbedPane tabPrincipal;
    private JPanel tabCartelera;
    private JPanel tabDulceria;
    private JPanel pnlFormPelicula;
    private JTextField txtCodigo;
    private JTextField txtDuracion;
    private JComboBox cbxFormato;
    private JTextField txtTitulo;
    private JTextField txtSala;
    private JTextField txtCapacidad;
    private JTextField txtPrecioBase;
    private JScrollPane pnlTablaCartelera;
    private JTextArea txaCartelera;
    private JPanel pnlBotones;
    private JButton btnRegistrarPeli;
    private JButton btnMostrarFunciones;
    private JButton btnVenderBoleto;
    private JButton btnPromoEstudiante;
    private JTextField txtCodDulceria;
    private JTextField txtNomDulceria;
    private JComboBox cbxTipoDulceria;
    private JTextField txtPrecioDulceria;
    private JTextArea txaDulceria;
    private JButton btnRegistrarDulceria;
    private JButton btnMostrarCatalogoDulceria;
    private JButton btnVenderDulceria;
    private JButton btnPromoDulceria;

    private final CineServicio cineServicio = new CineServicio();

    public VentanaCine() {
        setTitle("NovaCinema - Gestión de Cine");
        setContentPane(panelRaiz);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);

        setLocationRelativeTo(null);

        cbxFormato.setModel(new DefaultComboBoxModel<>(TipoFormato.values()));

        cbxTipoDulceria.setModel(new DefaultComboBoxModel<>(TipoProducto.values()));

        configurarEventos();
    }

    private void configurarEventos() {
        btnRegistrarPeli.addActionListener(e -> {
            try {
                String codigo = txtCodigo.getText().trim();
                String titulo = txtTitulo.getText().trim();
                int duracion = Integer.parseInt(txtDuracion.getText().trim());
                int sala = Integer.parseInt(txtSala.getText().trim());
                int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
                double precio = Double.parseDouble(txtPrecioBase.getText().trim());
                TipoFormato formato = (TipoFormato) cbxFormato.getSelectedItem();
                FuncionPelicula pelicula = new FuncionPelicula(codigo, titulo, formato,
                        precio, sala, duracion, capacidad);

                cineServicio.registrarPelicula(pelicula);

                actualizarCartelera();
                limpiarCamposCartelera();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Revise los campos numéticos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnVenderBoleto.addActionListener( e -> {
            String codigo = JOptionPane.showInputDialog(this,
                    "Ingrese el código de la funcion");

            if (codigo == null || codigo.isBlank()) {
                return;
            }

            Optional<FuncionPelicula> resultado = cineServicio.buscarPelicula(codigo.trim());

            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró una función con el código" + codigo,
                        "Sin Resultados",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            FuncionPelicula pelicula = resultado.get();

            String textoCantidad = JOptionPane.showInputDialog( this,
                    "Pelicula: " + pelicula.getNombre() + "\nButacas Disponibles: "
                            + pelicula.getAsientosDisponibles() + "\n\nCantidad de Boletos: "
            );

            if (textoCantidad == null || textoCantidad.isBlank()) {
                return;
            }

            try {
                int cantidad = Integer.parseInt(textoCantidad.trim());

                Double total = cineServicio.venderBoletos(codigo.trim(), cantidad);

                if (total == null) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo realizar la venta \n" +
                            "Verifique la cantidad y las butacas disponibles.",
                            "Venta no realizada",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                JOptionPane.showMessageDialog(this,
                        String.format("Venta realizada correctamente." +
                                "\n\nPelícula: %s" +
                                "\n\n Boletos: %d" +
                                "\nTotal: $%.2f",
                        pelicula.getNombre(), cantidad, total ),
                        "Taquilla",
                        JOptionPane.INFORMATION_MESSAGE
                );

                actualizarCartelera();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Debe ingresar una cantidad válida",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnRegistrarDulceria.addActionListener( e -> {
            try {
                String codigo = txtCodDulceria.getText().trim();
                String nombre = txtNomDulceria.getText().trim();
                double precio = Double.parseDouble(txtPrecioDulceria.getText().trim());
                TipoProducto tipo = (TipoProducto) cbxTipoDulceria.getSelectedItem();

                ProductoDulceria producto = new ProductoDulceria(codigo, nombre, tipo, precio);

                cineServicio.registrarProducto(producto);

                actualizarDulceria();

                limpiarCamposDulceria();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ingrese un precio válido.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnVenderDulceria.addActionListener(e -> {
            String codigo = JOptionPane.showInputDialog(this,
                    "Ingrese el código del producto o combo");

            if (codigo == null || codigo.isBlank()) {
                return;
            }

            Optional<ProductoDulceria> resultado = cineServicio.buscarProducto(codigo.trim());

            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró un producto con el código:" + codigo,
                        "Sin Resultados",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            ProductoDulceria producto = resultado.get();

            String textoCantidad = JOptionPane.showInputDialog(this,
                    "Producto: " + producto.getNombre() + "\nTipo: " + producto.getCategoria() +
                            "\nPrecio: " + String.format("%.2f", producto.calcularPrecio()) + "\nCantidad: ");

            if (textoCantidad == null || textoCantidad.isBlank()) {
                return;
            }

            try {
                int cantidad = Integer.parseInt(textoCantidad.trim());
                Double total = cineServicio.venderProducto(codigo.trim(), cantidad);

                if (total == null) {
                    JOptionPane.showMessageDialog(this,
                            "Venta realizada correctamente",
                            "Dulceria",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ingrese una cantidad válida.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });



    }
    private void actualizarCartelera() {
        StringBuilder sb = new StringBuilder("===== CARTELERA =====\n\n");

        if (cineServicio.obtenerCartelera().isEmpty()) {
            sb.append("(Aún no hay funciones registradas)");
        } else {
            for (FuncionPelicula pelicula : cineServicio.obtenerCartelera()) {
                sb.append(pelicula.getDetalle()).append("\n\n");
            }
        }

        txaCartelera.setText(sb.toString());
    }

    private void limpiarCamposCartelera() {
        txtCodigo.setText("");
        txtTitulo.setText("");
        txtDuracion.setText("");
        txtSala.setText("");
        txtCapacidad.setText("");
        txtPrecioBase.setText("");
        cbxFormato.setSelectedIndex(0);
    }

    private void actualizarDulceria() {
        StringBuilder sb = new StringBuilder("===== CATÁLOGO DE DULCERÍA =====\n\n");

        if (cineServicio.obtenerDulceria().isEmpty()) {
            sb.append("(Aún no hay productos registrados)");
        } else {
            for (ProductoDulceria producto : cineServicio.obtenerDulceria()) {
                sb.append(producto.getDetalle()).append("\n\n");
            }
        }

        txaDulceria.setText(sb.toString());
    }

    private void limpiarCamposDulceria() {
        txtCodDulceria.setText("");
        txtNomDulceria.setText("");
        txtPrecioDulceria.setText("");
        cbxTipoDulceria.setSelectedIndex(0);
    }
}


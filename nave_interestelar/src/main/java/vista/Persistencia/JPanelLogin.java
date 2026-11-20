package vista.Persistencia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import Controlador.AutenticacionController;
import modelo.Persistencia.UsuarioDTO;

public class JPanelLogin extends JPanel implements AutenticacionObserver {

    private final AutenticacionController controlador;
    private final JTextField txtUsuario;
    private final JPasswordField txtContrasena;
    private final JButton btnIngresar;
    private final JButton btnIrRegistro;
    private final JLabel lblEstado;

    public JPanelLogin(AutenticacionController controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo.");
        }
        this.controlador = controlador;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Inicio de Sesión", JLabel.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 18f));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Credenciales"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtUsuario = new JTextField(18);
        panelFormulario.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtContrasena = new JPasswordField(18);
        panelFormulario.add(txtContrasena, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        lblEstado = new JLabel(" ", JLabel.CENTER);
        panelFormulario.add(lblEstado, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnIrRegistro = new JButton("Registrarse");
        btnIngresar = new JButton("Ingresar");
        panelBotones.add(btnIrRegistro);
        panelBotones.add(btnIngresar);
        add(panelBotones, BorderLayout.SOUTH);

        configurarEventos();
        this.controlador.agregarObservador(this);
    }

    private void configurarEventos() {
        btnIngresar.addActionListener(e -> ejecutarLogin());
        txtContrasena.addActionListener(e -> ejecutarLogin());
        btnIrRegistro.addActionListener(e -> {
            limpiarCampos();
            controlador.irARegistro();
        });
    }

    private void ejecutarLogin() {
        String usuario = txtUsuario.getText();
        String pass = new String(txtContrasena.getPassword());
        controlador.iniciarSesion(usuario, pass);
    }

    public void limpiarCampos() {
        txtUsuario.setText("");
        txtContrasena.setText("");
        lblEstado.setText(" ");
    }

    @Override
    public void onAutenticacionExitosa(UsuarioDTO usuario) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setForeground(new Color(0, 128, 0));
            lblEstado.setText("Sesión iniciada como: " + usuario.getNombreUsuario());
            txtContrasena.setText("");
        });
    }

    @Override
    public void onRegistroResultado(boolean registrado, String mensaje) {
        if (registrado) {
            SwingUtilities.invokeLater(() -> {
                lblEstado.setForeground(new Color(0, 128, 0));
                lblEstado.setText(mensaje);
            });
        }
    }

    @Override
    public void onSesionCerrada() {
        SwingUtilities.invokeLater(this::limpiarCampos);
    }

    @Override
    public void onErrorAutenticacion(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setForeground(Color.RED);
            lblEstado.setText(mensaje);
        });
    }
}

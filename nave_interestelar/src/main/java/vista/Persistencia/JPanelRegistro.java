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

public class JPanelRegistro extends JPanel implements AutenticacionObserver {

    private final AutenticacionController controlador;
    private final JTextField txtUsuario;
    private final JPasswordField txtContrasena;
    private final JPasswordField txtConfirmar;
    private final JButton btnRegistrar;
    private final JButton btnVolverLogin;
    private final JLabel lblMensaje;

    public JPanelRegistro(AutenticacionController controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo.");
        }
        this.controlador = controlador;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Registro de Usuario", JLabel.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 18f));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Nueva cuenta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Nombre de usuario:"), gbc);

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
        gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Confirmar contraseña:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtConfirmar = new JPasswordField(18);
        panelFormulario.add(txtConfirmar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        lblMensaje = new JLabel(" ", JLabel.CENTER);
        panelFormulario.add(lblMensaje, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnVolverLogin = new JButton("Volver a Login");
        btnRegistrar = new JButton("Registrar");
        panelBotones.add(btnVolverLogin);
        panelBotones.add(btnRegistrar);
        add(panelBotones, BorderLayout.SOUTH);

        configurarEventos();
        this.controlador.agregarObservador(this);
    }

    private void configurarEventos() {
        btnRegistrar.addActionListener(e -> ejecutarRegistro());
        txtConfirmar.addActionListener(e -> ejecutarRegistro());
        btnVolverLogin.addActionListener(e -> {
            limpiarCampos();
            controlador.irALogin();
        });
    }

    private void ejecutarRegistro() {
        String usuario = txtUsuario.getText();
        String pass = new String(txtContrasena.getPassword());
        String confirm = new String(txtConfirmar.getPassword());
        controlador.registrarUsuario(usuario, pass, confirm);
    }

    public void limpiarCampos() {
        txtUsuario.setText("");
        txtContrasena.setText("");
        txtConfirmar.setText("");
        lblMensaje.setText(" ");
    }

    @Override
    public void onAutenticacionExitosa(UsuarioDTO usuario) {
        // No aplica al panel de registro
    }

    @Override
    public void onRegistroResultado(boolean registrado, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblMensaje.setForeground(registrado ? new Color(0, 128, 0) : Color.RED);
            lblMensaje.setText(mensaje);
            if (registrado) {
                txtContrasena.setText("");
                txtConfirmar.setText("");
            }
        });
    }

    @Override
    public void onSesionCerrada() {
        SwingUtilities.invokeLater(this::limpiarCampos);
    }

    @Override
    public void onErrorAutenticacion(String mensaje) {
        // No aplica al panel de registro
    }
}

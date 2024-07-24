package com.crabchat_sd.crabchatfinalproy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ClienteLogin {
    public static void main(String[] args) {
        MarcoLogin miMarco = new MarcoLogin();
        miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void iniciarSesionAutomaticamente(String user, String pass, JFrame ventana) {
        try {
            URL url = new URL("http://localhost:3000/login");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String urlParameters = "identifier=" + URLEncoder.encode(user, "UTF-8") +
                                   "&password=" + URLEncoder.encode(pass, "UTF-8");

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = urlParameters.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Inicio de sesión exitoso: " + response.toString());

                    // Cerrar la ventana actual y proceder al chat
                    if (ventana != null) {
                        ventana.dispose();
                    }
                    String[] args = {};
                    ClienteChat.main(args);
                }
            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Error en el inicio de sesión: " + response.toString());
                    JOptionPane.showMessageDialog(null, "Credenciales inválidas o error en el servidor: " + response.toString());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al conectar con el servidor.");
        }
    }
}

class MarcoLogin extends JFrame {
    public MarcoLogin() {
        setTitle("APP CLIENTE");
        setBounds(100, 50, 270, 450);
        LaminaLogin lamina1 = new LaminaLogin(this);
        add(lamina1);
        setVisible(true);
    }
}

class LaminaLogin extends JPanel {
    private JTextField usuario;
    private JPasswordField contrasena;
    private JButton botonLogin, botonRegister;
    private JFrame marcoLogin;

    public LaminaLogin(JFrame marcoLogin) {
        this.marcoLogin = marcoLogin;

        JLabel texto1 = new JLabel("Usuario");
        add(texto1);

        usuario = new JTextField(20);
        add(usuario);

        JLabel texto2 = new JLabel("Contraseña");
        add(texto2);

        contrasena = new JPasswordField(20);
        add(contrasena);

        botonLogin = new JButton("Iniciar Sesion");
        botonLogin.addActionListener(new IniciarSesion());
        add(botonLogin);

        botonRegister = new JButton("Registrar Nuevo");
        botonRegister.addActionListener(new AbrirRegister());
        add(botonRegister);
    }

    private class IniciarSesion implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String user = usuario.getText();
            String pass = new String(contrasena.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
                return;
            }
            ClienteLogin.iniciarSesionAutomaticamente(user, pass, marcoLogin);
        }
    }

    private class AbrirRegister implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = {};

            // Se cierra esta interfaz y se inicia el register
            marcoLogin.dispose();
            ClienteRegister.main(args);
        }
    }
}

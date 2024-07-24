package com.crabchat_sd.crabchatfinalproy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
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
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class ClienteRegister {
    public static void main(String[] args) {    
        MarcoRegister miMarco = new MarcoRegister();
        miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
    }
}

class MarcoRegister extends JFrame {
    public MarcoRegister() {
        setTitle("APP CLIENTE");
        setBounds(100, 50, 270, 500);
        LaminaRegister lamina1 = new LaminaRegister(this);  // Pasar la referencia
        add(lamina1);
        setVisible(true);
    }
}

class LaminaRegister extends JPanel {
    private JTextField username, firstname, lastname, email, birthday;
    private JPasswordField password;
    private JButton botonRegister;
    private JFrame marcoRegister;  // Referencia al marco de registro

    public LaminaRegister(JFrame marcoRegister) {
        this.marcoRegister = marcoRegister;

        JLabel texto1 = new JLabel("Nombre");
        add(texto1);

        firstname = new JTextField(20);
        add(firstname);

        JLabel texto2 = new JLabel("Apellido");
        add(texto2);

        lastname = new JTextField(20);
        add(lastname);

        JLabel texto3 = new JLabel("Nombre de usuario");
        add(texto3);

        username = new JTextField(20);
        add(username);

        JLabel texto4 = new JLabel("Correo");
        add(texto4);

        email = new JTextField(20);
        add(email);

        JLabel texto5 = new JLabel("Contraseña");
        add(texto5);

        password = new JPasswordField(20); // Cambiado a JPasswordField
        add(password);

        JLabel texto6 = new JLabel("Fecha de Nacimiento (yyyy-mm-dd)");
        add(texto6);

        birthday = new JTextField(20);
        add(birthday);

        botonRegister = new JButton("Registrarse");
        Registrarse eventoRegister = new Registrarse();
        botonRegister.addActionListener(eventoRegister);
        add(botonRegister);
    }

    private class Registrarse implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String user = username.getText();
            String pass = new String(password.getPassword()); // Obtener la contraseña como String
            String first = firstname.getText();
            String last = lastname.getText();
            String mail = email.getText();
            String birth = birthday.getText();

            if (user.isEmpty() || pass.isEmpty() || first.isEmpty() || last.isEmpty() || mail.isEmpty() || birth.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
                return;
            }

            try {
                URL url = new URL("http://localhost:3001/register");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                // Codificar los datos en URL
                String urlParameters = "first_name=" + URLEncoder.encode(first, "UTF-8") +
                                       "&last_name=" + URLEncoder.encode(last, "UTF-8") +
                                       "&birthday=" + URLEncoder.encode(birth, "UTF-8") +
                                       "&username=" + URLEncoder.encode(user, "UTF-8") +
                                       "&password=" + URLEncoder.encode(pass, "UTF-8") +
                                       "&email=" + URLEncoder.encode(mail, "UTF-8");

                System.out.println("URL Parameters: " + urlParameters);

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = urlParameters.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush(); // Asegurarse de que se envíen todos los datos
                }

                int responseCode = con.getResponseCode();
                System.out.println("Código de respuesta: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    // Intentar leer el InputStream solo si no está vacío
                    InputStream inputStream = con.getInputStream();
                    if (inputStream != null && inputStream.available() > 0) {
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(inputStream, "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            System.out.println("Registro exitoso: " + response.toString());
                        }
                    }

                    // Cerrar la ventana de registro y abrir la de login
                    marcoRegister.dispose();
                    
                    ClienteLogin.iniciarSesionAutomaticamente(user,pass, marcoRegister);
                } else {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getErrorStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        System.out.println("Error en el registro: " + response.toString());
                        JOptionPane.showMessageDialog(null, "Error en el registro o en el servidor: " + response.toString());
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al conectar con el servidor.");
            }
        }
    }
}

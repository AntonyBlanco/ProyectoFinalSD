package com.crabchat_sd.crabchatfinalproy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteChat {
    public static void main(String[]args) {	
        MarcoCliente miMarco = new MarcoCliente();
        miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
    }
}

class MarcoCliente extends JFrame {
    public MarcoCliente() {
        setTitle("APP CLIENTE");

        setBounds(100,50,270,450);

        LaminaCliente lamina1 = new LaminaCliente();

        add(lamina1);

        setVisible(true);
    }
}

class LaminaCliente extends JPanel implements Runnable {
    private JTextField campo1,nick,ip;
    private JButton miBoton;
    private JTextArea campoChat;

    public LaminaCliente() {
        nick = new JTextField(7);
        add(nick);

        JLabel texto = new JLabel("Chat");
        add(texto);

        ip = new JTextField(9);
        add(ip);

        campoChat = new JTextArea(18,20);
        add(campoChat);

        campo1 = new JTextField(20);
        add(campo1);

        miBoton = new JButton("Enviar");

        EnviaTexto miEvento = new EnviaTexto();

        miBoton.addActionListener(miEvento);

        add(miBoton);

        Thread miHilo = new Thread(this);
        miHilo.start();
    }

    private class EnviaTexto implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            campoChat.append("\n" + campo1.getText());

            try {
                Socket miSocket = new Socket("192.168.83.1",9999);

                PaqueteEnvio datos = new PaqueteEnvio();

                datos.setNick(nick.getText());
                datos.setIp(ip.getText());
                datos.setMensaje(campo1.getText());

                ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream());

                paqueteDatos.writeObject(datos);
                miSocket.close();

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                System.out.println(e1.getMessage());
            }
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket servidorCliente = new ServerSocket(9090);

            Socket cliente;

            PaqueteEnvio paqueteRecibido;

            while(true) {
                cliente =servidorCliente.accept();

                ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());

                paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject(); 

                campoChat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class PaqueteEnvio implements Serializable {
    private String nick,ip,mensaje;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

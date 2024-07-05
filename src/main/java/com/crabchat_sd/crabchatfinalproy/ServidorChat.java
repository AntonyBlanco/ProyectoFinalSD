package com.crabchat_sd.crabchatfinalproy;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ServidorChat {
    public static void main(String[]args) {
        MarcoServidor miMarco = new MarcoServidor();
        miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoServidor extends JFrame implements Runnable {
    private JTextArea areaTexto;

    public MarcoServidor() {
        setTitle("APP SERVIDOR");
        setBounds(100,100,270,450);
        JPanel lamina1 = new JPanel();

        lamina1.setLayout(new BorderLayout());
        areaTexto = new JTextArea();
        lamina1.add(areaTexto,BorderLayout.CENTER);
        add(lamina1);
        setVisible(true);	

        Thread miHilo = new Thread(this);
        miHilo.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket servidor = new ServerSocket(9999);

            String nick,ip,mensaje;

            PaqueteEnvio paqueteRecibido;

            while(true) {

                Socket miSocket = servidor.accept();

                ObjectInputStream paqueteDatos = new ObjectInputStream(miSocket.getInputStream()); 

                paqueteRecibido = (PaqueteEnvio) paqueteDatos.readObject();

                nick=paqueteRecibido.getNick();
                ip=paqueteRecibido.getIp();
                mensaje=paqueteRecibido.getMensaje();

                areaTexto.append("\n" + nick + ":" + "\n" + mensaje + "\npara IP: " + ip);

                Socket enviaDestinatario = new Socket(ip,9090); 

                ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream()); 

                paqueteReenvio.writeObject(paqueteRecibido);
                paqueteReenvio.close();

                enviaDestinatario.close();

                miSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

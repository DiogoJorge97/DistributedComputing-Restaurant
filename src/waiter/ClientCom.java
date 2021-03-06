package waiter;

import java.io.*;
import java.net.*;

/**
 * Implements the communication channel (client side) Allows for a communication
 * based on TCP protocol
 *
 */
public class ClientCom {

    /**
     * Communication socket
     *
     * @serialField commSocket
     */
    private Socket commSocket = null;

    /**
     * Server host name
     *
     * @serialField serverPortNumb
     */
    private String serverHostName = null;


    /**
     * Server port number 
     *
     * @serialField serverPortNumb
     */
    private int serverPortNumb;
    
    /**
     * Communication channel entering stream
     *
     * @serialField in
     */
    private ObjectInputStream in = null;

    /**
     * Communication channel exiting stream
     *
     * @serialField out
     */
    private ObjectOutputStream out = null;

    /**
     * Instantiation of a communication channel
     *
     * @param hostName Server side host name 
     * @param portNumb Server side port number 
     */
    public ClientCom(String hostName, int portNumb) {
        serverHostName = hostName;
        serverPortNumb = portNumb;
    }

    /**
     * Open communications channel 
     *
     * @return true, if the communications channel was successfully opened, false if not  
     */
    public boolean open() {
        boolean success = true;
        SocketAddress serverAddress = new InetSocketAddress(serverHostName, serverPortNumb);

        try {
            commSocket = new Socket();
            commSocket.connect(serverAddress);
        } catch (UnknownHostException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - o nome do sistema computacional onde reside o servidor é desconhecido: "
                    + serverHostName + "!");
            System.exit(1);
        } catch (NoRouteToHostException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - o nome do sistema computacional onde reside o servidor é inatingível: "
                    + serverHostName + "!");
            System.exit(1);
        } catch (ConnectException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - o servidor não responde em: " + serverHostName + "." + serverPortNumb + "!");
            if (e.getMessage().equals("Connection refused")) {
                success = false;
            } else {
                System.out.println(e.getMessage() + "!");
                System.exit(1);
            }
        } catch (SocketTimeoutException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - ocorreu um time out no estabelecimento da ligação a: "
                    + serverHostName + "." + serverPortNumb + "!");
            success = false;
        } catch (IOException e) // erro fatal --- outras causas
        {
            System.out.println(Thread.currentThread().getName()
                    + " - ocorreu um erro indeterminado no estabelecimento da ligação a: "
                    + serverHostName + "." + serverPortNumb + "!");
            System.exit(1);
        }

        if (!success) {
            return (success);
        }

        try {
            out = new ObjectOutputStream(commSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - não foi possível abrir o canal de saída do socket!");
            System.exit(1);
        }

        try {
            in = new ObjectInputStream(commSocket.getInputStream());
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - não foi possível abrir o canal de entrada do socket!");
            System.exit(1);
        }

        return (success);
    }

    /**
     * Closes communications channel 
     *
     */
    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - não foi possível fechar o canal de entrada do socket!");
            System.exit(1);
        }

        try {
            out.close();
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - não foi possível fechar o canal de saída do socket!");
            System.exit(1);
        }

        try {
            commSocket.close();
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - não foi possível fechar o socket de comunicação!");
            System.exit(1);
        }
    }

    /**
     * Communication channel object reader
     *
     * @return red object
     */
    public Object readObject() {
        Object fromServer = null;                            // objecto

        try {
            fromServer = in.readObject();
        } catch (InvalidClassException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - o objecto lido não é passível de desserialização!");
            System.exit(1);
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - erro na leitura de um objecto do canal de entrada do socket de comunicação!");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - o objecto lido corresponde a um tipo de dados desconhecido!");
            System.exit(1);
        }

        return fromServer;
    }

     /**
     * Communication channel object writer
     *
     * @param toServer object to be written
     */
    public void writeObject(Object toServer) {
        try {
            out.writeObject(toServer);
        } catch (InvalidClassException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - o objecto a ser escrito não é passível de serialização!");
            System.exit(1);
        } catch (NotSerializableException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - o objecto a ser escrito pertence a um tipo de dados não serializável!");
            System.exit(1);
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName()
                    + " - erro na escrita de um objecto do canal de saída do socket de comunicação!");
            System.exit(1);
        }
    }
}

package kitchen;

import comInf.Message;
import comInf.MessageException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class defines the service agent 
 * The communication is based on sending and receiving messages using TCP protocol  
 *
 */
public class ClientProxy extends Thread {

    /**
     * Threads counter
     *
     * @serialField nProxy
     */

    private static int nProxy = 0;

    /**
     * Communication channel 
     *
     * @serialField sconi
     */
    private ServerCom sconi;

    /**
     * Kitchen Interface 
     *
     * @serialField kitchenInterface
     */
    private KitchenInterface kitchenInterface;

    /**
     * Creation of interface instance
     *
     * @param sconi Communication channel
     * @param kitchenInterface Kitchen Interface
     */
    public ClientProxy(ServerCom sconi, KitchenInterface kitchenInterface) {
        super("Proxy_" + ClientProxy.getProxyId());

        this.sconi = sconi;
        this.kitchenInterface = kitchenInterface;
    }


    /**
     * Service agent thread life cycle
     */
    @Override
    public void run() {
        Message inMessage = null, // mensagem de entrada
                outMessage = null;                      // mensagem de saída

        inMessage = (Message) sconi.readObject();                     // ler pedido do cliente
        try {
            outMessage = kitchenInterface.processAndReply(inMessage);         // processá-lo
        } catch (MessageException e) {
            System.out.println("Thread " + getName() + ": " + e.getMessage() + "!");
            System.out.println(e.getMessageVal().toString());
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(ClientProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        sconi.writeObject(outMessage);                                // enviar resposta ao cliente
        sconi.close();                                                // fechar canal de comunicação
    }

    /**
     * Instantiation identifier
     *
     * @return Proxy identifier
     */
    private static int getProxyId() {
        Class<?> cl = null;                                  // representação do tipo de dados ClientProxy na máquina
        //   virtual de Java
        int proxyId;                                         // identificador da instanciação

        try {
            cl = Class.forName("kitchen.ClientProxy");
        } catch (ClassNotFoundException e) {
            System.out.println("O tipo de dados ClientProxy não foi encontrado!");
            System.exit(1);
        }

        synchronized (cl) {
            proxyId = nProxy;
            nProxy += 1;
        }

        return proxyId;
    }

    /**
     * Communication channel getter 
     *
     * @return Communication channel
     */
    public ServerCom getScon() {
        return sconi;
    }
}

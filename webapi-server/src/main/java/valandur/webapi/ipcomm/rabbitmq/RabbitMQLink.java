package valandur.webapi.ipcomm.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import valandur.webapi.ipcomm.IPLink;
import valandur.webapi.ipcomm.IPRequest;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeoutException;

public class RabbitMQLink extends IPLink {

    private Channel connChannel;
    private Channel recvChannel;
    private Channel sendChannel;
    private Set<String> servers = new ConcurrentSkipListSet<>();


    @Override
    public void init(ContextHandlerCollection handlers) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("localhost");

        try {
            Connection conn = factory.newConnection();

            sendChannel = conn.createChannel();
            sendChannel.exchangeDeclare("request", BuiltinExchangeType.DIRECT, true);

            connChannel = conn.createChannel();
            connChannel.queueDeclare("connect", true, false, false, null);
            connChannel.basicConsume("connect", new DefaultConsumer(connChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String msg = new String(body);
                    System.out.println("Connected: " + msg);
                    servers.add(msg);

                    connChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            });
            connChannel.queueDeclare("disconnect", true, false, false, null);
            connChannel.basicConsume("disconnect", new DefaultConsumer(connChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String msg = new String(body);
                    System.out.println("Disonnected: " + msg);
                    servers.remove(msg);

                    connChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            });

            recvChannel = conn.createChannel();
            recvChannel.queueDeclare("response", true, false, false, null);
            recvChannel.basicConsume("response", new DefaultConsumer(recvChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String res = new String(body);
                    System.out.println("Got: " + res);

                    respond(res);

                    long deliveryTag = envelope.getDeliveryTag();
                    recvChannel.basicAck(deliveryTag, false);
                }
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasServer(String server) {
        return servers.contains(server);
    }

    @Override
    public void send(String server, IPRequest message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String msg = mapper.writeValueAsString(message);
            sendChannel.basicPublish("request", server, null, msg.getBytes());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

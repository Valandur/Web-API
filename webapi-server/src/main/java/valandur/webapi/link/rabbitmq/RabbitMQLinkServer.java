package valandur.webapi.link.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import valandur.webapi.link.LinkServer;
import valandur.webapi.link.message.ConnectMessage;
import valandur.webapi.link.message.DisconnectMessage;
import valandur.webapi.link.message.RequestMessage;
import valandur.webapi.link.message.ResponseMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMQLinkServer extends LinkServer {

    private Channel connChannel;
    private Channel recvChannel;
    private Channel sendChannel;


    public RabbitMQLinkServer(Map<String, String> serverKeys) {
        super(serverKeys);
    }

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
                    ObjectMapper mapper = new ObjectMapper();
                    ConnectMessage msg = mapper.readValue(new String(body), ConnectMessage.class);

                    addServer(msg.getKey());

                    connChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            });
            connChannel.queueDeclare("disconnect", true, false, false, null);
            connChannel.basicConsume("disconnect", new DefaultConsumer(connChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    ObjectMapper mapper = new ObjectMapper();
                    DisconnectMessage msg = mapper.readValue(new String(body), DisconnectMessage.class);

                    removeServer(msg.getKey());

                    connChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            });

            recvChannel = conn.createChannel();
            recvChannel.queueDeclare("response", true, false, false, null);
            recvChannel.basicConsume("response", new DefaultConsumer(recvChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    ObjectMapper mapper = new ObjectMapper();
                    ResponseMessage msg = mapper.readValue(new String(body), ResponseMessage.class);

                    respond(msg);

                    long deliveryTag = envelope.getDeliveryTag();
                    recvChannel.basicAck(deliveryTag, false);
                }
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRequest(String serverName, RequestMessage message) {
        String serverKey = serverKeys.get(serverName);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String msg = mapper.writeValueAsString(message);
            sendChannel.basicPublish("request", serverKey, null, msg.getBytes());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

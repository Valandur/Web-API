package valandur.webapi.link.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import valandur.webapi.WebAPI;
import valandur.webapi.link.LinkClient;
import valandur.webapi.link.message.*;
import valandur.webapi.util.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class RabbitMQClient extends LinkClient {

    private String id = Util.generateUniqueId();
    private Channel channel;


    @Override
    public void connect(String target) {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUri(target);
            Connection conn = factory.newConnection();

            // Setup request exchange
            channel = conn.createChannel();
            channel.exchangeDeclare("request", BuiltinExchangeType.DIRECT, true);

            // Bind our queue to the exchange
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "request", id);

            // Send "connect" event to main server
            sendConnect(new ConnectMessage(privateKey));

            // Consume incoming request messages
            channel.basicConsume(queueName, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String msg =  new String(body);
                    ObjectMapper mapper = new ObjectMapper();

                    RequestMessage message = mapper.readValue(msg, RequestMessage.class);
                    ResponseMessage res = WebAPI.emulateRequest(message);
                    sendResponse(res);

                    long deliveryTag = envelope.getDeliveryTag();
                    channel.basicAck(deliveryTag, false);
                }
            });
        } catch (IOException | TimeoutException | NoSuchAlgorithmException | KeyManagementException |
                URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (channel == null) {
            return;
        }

        sendDisconnect(new DisconnectMessage(privateKey));
    }

    @Override
    protected void sendConnect(ConnectMessage message) {
        send("connect", message);
    }

    @Override
    protected void sendDisconnect(DisconnectMessage message) {
        send("disconnect", message);
    }

    @Override
    protected void sendResponse(ResponseMessage message) {
        send("response", message);
    }

    private void send(String channel, BaseMessage message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.channel.basicPublish("", channel, null, mapper.writeValueAsBytes(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package valandur.webapi.ipcomm.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import valandur.webapi.WebAPI;
import valandur.webapi.ipcomm.IPClient;
import valandur.webapi.ipcomm.IPRequest;
import valandur.webapi.ipcomm.IPResponse;
import valandur.webapi.util.Util;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class RabbitMQClient implements IPClient {

    private String id = Util.generateUniqueId();
    private Channel channel;


    @Override
    public void connect(String target) {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUri(target);
            Connection conn = factory.newConnection();

            channel = conn.createChannel();
            channel.exchangeDeclare("request", BuiltinExchangeType.DIRECT, true);

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "request", id);

            channel.basicPublish("", "connect", null, id.getBytes());
            channel.basicConsume(queueName, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String msg =  new String(body);
                    System.out.println("Got: " + msg);

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        IPRequest message = mapper.readValue(msg, IPRequest.class);
                        IPResponse res = WebAPI.emulateRequest(message);
                        String resString = mapper.writeValueAsString(res);
                        channel.basicPublish("", "response", null, resString.getBytes());
                    } catch (IOException | ServletException e) {
                        e.printStackTrace();
                    }

                    long deliveryTag = envelope.getDeliveryTag();
                    channel.basicAck(deliveryTag, false);
                }
            });
        } catch (IOException | TimeoutException | NoSuchAlgorithmException | KeyManagementException |
                URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

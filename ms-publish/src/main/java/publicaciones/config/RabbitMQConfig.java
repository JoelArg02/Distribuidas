package publicaciones.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CLOCK_REQUEST_QUEUE = "reloj.solicitd";

    @Bean
    public Queue solicitud() {
        return QueueBuilder.durable(CLOCK_REQUEST_QUEUE).build();
    }
}

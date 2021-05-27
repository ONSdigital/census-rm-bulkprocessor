package uk.gov.ons.census.bulkprocessor.config;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.ons.census.bulkprocessor.utility.ObjectMapperFactory;

@Configuration
@EnableScheduling
public class AppConfig {
  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    rabbitTemplate.setChannelTransacted(true);
    return rabbitTemplate;
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter(ObjectMapperFactory.objectMapper());
  }

  @Bean
  RMQConnectionFactory rmqConnectionFactory() {
    return new RMQConnectionFactory();
  }

  @Bean
  JmsTemplate jmsTemplate(RMQConnectionFactory rmqConnectionFactory) {
    return new JmsTemplate(rmqConnectionFactory);
  }

  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}

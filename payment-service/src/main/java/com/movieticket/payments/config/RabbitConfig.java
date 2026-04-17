package com.movieticket.payments.config;

import com.movieticket.contracts.RabbitConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange movieTicketExchange() {
        return new DirectExchange(RabbitConstants.EXCHANGE_MOVIE_TICKET, true, false);
    }

    @Bean
    public Queue paymentBookingCreatedQueue() {
        return new Queue(RabbitConstants.Q_PAYMENT_BOOKING_CREATED, true);
    }

    @Bean
    public Binding bindingBookingCreated(Queue paymentBookingCreatedQueue, DirectExchange movieTicketExchange) {
        return BindingBuilder.bind(paymentBookingCreatedQueue)
                .to(movieTicketExchange)
                .with(RabbitConstants.RK_BOOKING_CREATED);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}

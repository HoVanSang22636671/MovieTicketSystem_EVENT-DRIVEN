package com.movieticket.notifications.config;

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
    public Queue notificationPaymentCompletedQueue() {
        return new Queue(RabbitConstants.Q_NOTIFICATION_PAYMENT_COMPLETED, true);
    }

    @Bean
    public Queue notificationBookingFailedQueue() {
        return new Queue(RabbitConstants.Q_NOTIFICATION_BOOKING_FAILED, true);
    }

    @Bean
    public Binding bindingPaymentCompleted(Queue notificationPaymentCompletedQueue, DirectExchange movieTicketExchange) {
        return BindingBuilder.bind(notificationPaymentCompletedQueue)
                .to(movieTicketExchange)
                .with(RabbitConstants.RK_PAYMENT_COMPLETED);
    }

    @Bean
    public Binding bindingBookingFailed(Queue notificationBookingFailedQueue, DirectExchange movieTicketExchange) {
        return BindingBuilder.bind(notificationBookingFailedQueue)
                .to(movieTicketExchange)
                .with(RabbitConstants.RK_BOOKING_FAILED);
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

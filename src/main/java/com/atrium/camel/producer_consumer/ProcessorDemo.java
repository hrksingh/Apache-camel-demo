package com.atrium.camel.producer_consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class ProcessorDemo {

	public static void main(String[] args) throws Exception {

		CamelContext camel = new DefaultCamelContext();

		camel.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("direct:start")
				.process(new Processor() {
					
					@Override
					public void process(Exchange exchange) throws Exception {
						System.out.println("Firng Processor...........");
					}
				}).to("seda:end");
			}
		});
		
		camel.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("seda:start")
				.process(new Processor() {
					
					@Override
					public void process(Exchange exchange) throws Exception {
						String body = exchange.getIn().getBody(String.class);
						int[] surrogates = {0xD83D, 0xDC7D};
						String alienEmojiString = new String(surrogates, 0, surrogates.length);
						exchange.getMessage().setBody(body.toUpperCase() +" >>> I got uppercased in between by processor "+alienEmojiString);
					}
				}).to("seda:end");
			}
		});

		camel.start();

		ProducerTemplate producerTemplate = camel.createProducerTemplate();
		ConsumerTemplate consumerTemplate = camel.createConsumerTemplate();

		producerTemplate.sendBody("direct:start", "One produces One consumes >>>>[Sync]");
		String receiveBodyMsg1 = consumerTemplate.receiveBody("seda:end", String.class);
		
		producerTemplate.sendBody("seda:start", "i am lowercase ");
		String receiveBodyMsg2 = consumerTemplate.receiveBody("seda:end", String.class);

		System.out.println(receiveBodyMsg1);
		System.out.println(receiveBodyMsg2);

		camel.close();

	}
}

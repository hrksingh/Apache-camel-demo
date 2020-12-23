package com.atrium.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class ProducerConsumer {

	public static void main(String[] args) throws Exception {

		CamelContext camel = new DefaultCamelContext();

		camel.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("direct:start").to("seda:end");
				from("seda:start").to("seda:end");
			}
		});

		camel.start(); 
		
		/************************
		 seda >>> it send message asynchronously
		 
		 direct >>> send message synchronously 
		 
		************************/
		
		ProducerTemplate producerTemplate = camel.createProducerTemplate();
		ConsumerTemplate consumerTemplate = camel.createConsumerTemplate();

		producerTemplate.sendBody("seda:start", "One produces One consumes >>>>> [Async]");
		String receiveBodyMsg = consumerTemplate.receiveBody("seda:end", String.class);

		producerTemplate.sendBody("direct:start", "One produces One consumes >>>>[Sync]");
		String receiveBodyMsg2 = consumerTemplate.receiveBody("seda:end", String.class);

		System.out.println(receiveBodyMsg);
		System.out.println(receiveBodyMsg2);

		camel.close();

	}
}

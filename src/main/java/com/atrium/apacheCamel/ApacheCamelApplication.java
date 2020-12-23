package com.atrium.apacheCamel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApacheCamelApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApacheCamelApplication.class, args);

		CamelContext camel = new DefaultCamelContext();

		camel.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("timer:foo").log("Hello Camel");
			}
		});

		camel.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				/*
				 * //?noop=true from("file:data/input") .to("file:data/output");
				 */

				from("timer://simpleTimer?period=1000").setBody(simple("Hello from timer at ${header.firedTime}"))
						.to("stream:out");
			}
		});

		camel.start();

		System.out.println("Running for 10 seconds and then stopping");
		Thread.sleep(10000);

		camel.stop();
		camel.close();
	}

}

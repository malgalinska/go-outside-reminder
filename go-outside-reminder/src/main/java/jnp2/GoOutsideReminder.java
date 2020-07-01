package jnp2;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import org.apache.camel.component.weather.WeatherConstants;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public final class GoOutsideReminder {

    private GoOutsideReminder() {        
    }

	private static String file;
	private static String[] tasks;
	private static int alreadyDone;
	private static String currentTask;
    
    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();

		// Wysyłanie pogody po włączeniu programu.
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start")
					// Tu trzeba ustawić swoje miasto.
  					.to("weather:foo?location=Warsaw,Poland&appid=9504b354b6a9551420ec87b4d2f6b287&geolocationAccessKey=9893265fa3c4af0d35f20157a38639e7&geolocationRequestHostIP=178.42.20.107")
  					.process(new Processor() {
  						public void process(Exchange msg) throws Exception {
  							String weatherInfo = msg.getIn().getBody(String.class);

  							JSONParser parser = new JSONParser();
  							JSONObject info = (JSONObject)parser.parse(weatherInfo);
  							String sky = ((JSONObject)(((JSONArray)(info.get("weather"))).get(0))).get("description").toString();
  							Double temp = Double.parseDouble(((JSONObject)(info.get("main"))).get("temp").toString()) - 273.15;

  							msg.getIn().setBody("Look, today's weather is: " + sky + " and " + temp + "°C.");
  						}
  					})
  					.setHeader("subject", simple("Remember to go outside"))
					// Tu trzeba ustawić swojego maila.
					.setHeader("to", simple("go.outside.reminder.test@gmail.com"))
  					.to("smtps://smtp.gmail.com:465?username=go.outside.reminder.test@gmail.com&password=Test12345678");
			}
        });

		// Pobieranie listy zadań z pliku.
		context.addRoutes(new RouteBuilder() {
			public void configure() {
				// Tu trzeba ustawić swoją ścieżkę do pliku
				from("file:///home/malgorzatka/Pulpit/test/?fileName=lista.txt&charset=utf-8&noop=true")
					.process(new Processor() {
						public void process(Exchange msg) throws Exception {
							file = msg.getIn().getBody(String.class);
							tasks = file.split("\n");
							alreadyDone = Integer.parseInt(tasks[0]);
							if (alreadyDone < tasks.length - 1)
								currentTask = tasks[alreadyDone + 1];
							else
								currentTask = "Relax.";
						}
					});
			}
		});

		// Wysyłanie co określoną liczbę min przypomnienia o obecnym zadaniu.
		context.addRoutes(new RouteBuilder() {
			public void configure() {
				from("timer://foo?fixedRate=true&delay=10000&period=60000") // Tu ustw liczba minut * 60000
					.process(new Processor() {
						public void process(Exchange msg) throws Exception {
							msg.getIn().setBody(currentTask + " - You should do it now.");
						}
					})
					.setHeader("subject", simple("You have some work to do."))
					// Tu trzeba ustawić swojego maila.
					.setHeader("to", simple("go.outside.reminder.test@gmail.com"))
  					.to("smtps://smtp.gmail.com:465?username=go.outside.reminder.test@gmail.com&password=Test12345678");
			
			}
		});

		// Sprawdzanie, co minutę, czy nie przyszła wiadomość o obecnym zadaniu.
		context.addRoutes(new RouteBuilder() {
			public void configure() {
				from("imaps://imap.gmail.com?username=go.outside.reminder.test@gmail.com&password=Test12345678" + 
				     "&delete=false&unseen=true&delay=60000")
					.process(new Processor() {
						public void process(Exchange msg) throws Exception {
							String mail = msg.getIn().getBody(String.class);

							if (mail.equalsIgnoreCase("done")) {
								String[] parts = file.split("\n", 2);
								alreadyDone++;
								if (alreadyDone < tasks.length - 1)
									currentTask = tasks[alreadyDone + 1];
								else
									currentTask = "Relax.";
								file = alreadyDone + "\n" + parts[1];
							}
							msg.getIn().setBody(file);
						}
					})
					// Tu trzeba ustawić swoją ścieżkę do pliku
					.to("file:///home/malgorzatka/Pulpit/test/?fileName=lista.txt&charset=utf-8");
			}
		});

        ProducerTemplate template = context.createProducerTemplate();

        context.start();

        template.sendBody("direct:start", "");

		while(true);
    }
}


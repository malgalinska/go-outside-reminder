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
    
    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start")
  					.to("weather:foo?location=Warsaw,Poland&appid=9504b354b6a9551420ec87b4d2f6b287&geolocationAccessKey=9893265fa3c4af0d35f20157a38639e7&geolocationRequestHostIP=178.42.20.107")
  					.process(new Processor() {
  						public void process(Exchange exchange) throws Exception {
  							String weatherInfo = exchange.getIn().getBody(String.class);

  							JSONParser parser = new JSONParser();
  							JSONObject info = (JSONObject)parser.parse(weatherInfo);
  							String sky = ((JSONObject)(((JSONArray)(info.get("weather"))).get(0))).get("description").toString();
  							Double temp = Double.parseDouble(((JSONObject)(info.get("main"))).get("temp").toString()) - 273.15;

  							exchange.getIn().setBody("Look, today's weather is: " + sky + " and " + temp + "°C.");
  						}
  					})
  					.setHeader("subject", simple("Remember to go outside"))
					.setHeader("to", simple("go.outside.reminder.test@gmail.com"))
  					.to("smtps://smtp.gmail.com:465?username=go.outside.reminder.test@gmail.com&password=Test12345678");
			}
        });

        ProducerTemplate template = context.createProducerTemplate();

        context.start();

        template.sendBody("direct:start", "");

        context.stop();
    }
}
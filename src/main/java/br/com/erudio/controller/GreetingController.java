package br.com.erudio.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.model.Greeting;

@RestController
class GreetingController {

	private  static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
	// http://localhost:8080/greeting?name=Leandro
	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "Word") String name){
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
}

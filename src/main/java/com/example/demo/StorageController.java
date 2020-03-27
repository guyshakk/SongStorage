package com.example.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StorageController {
	private Map<String, Object> storage;
	private AtomicLong idGenerator;

	@PostConstruct
	public void init() {
		this.idGenerator = new AtomicLong(1);
		storage = new HashMap<String, Object>();
	}

	@RequestMapping(path = "/storage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ObjectWithKey store(@RequestBody Object object) {
		String newKey = "" + this.idGenerator.getAndIncrement();
		ObjectWithKey rv = new ObjectWithKey(newKey, object);
		storage.put(newKey, object);
		return rv;
	}

	@RequestMapping(path = "/storage/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(@PathVariable("id") String id, @RequestBody Object object) {
		storage.put(id, object);
	}

	@RequestMapping(path = "/storage/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object get(@PathVariable("id") String id) {
		if (storage.get(id) != null)
			return storage.get(id);
		else
			throw new DataNotFoundException("could not find item by key: " + id);
	}

	@RequestMapping(path = "/storage", method = RequestMethod.DELETE)
	public void clearAllData() {
		storage.clear();
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public Map<String, Object> handleError(DataNotFoundException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "Item not found";
		}

		return Collections.singletonMap("error", message);
	}

}
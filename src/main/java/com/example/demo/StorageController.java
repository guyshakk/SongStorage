package com.example.demo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

	private Map<String, Map<String, Object>> storage;

	@PostConstruct
	public void init() {
		this.storage = Collections.synchronizedMap(new TreeMap<>());
	}

	@RequestMapping(path = "/storage/{id}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Object store(@PathVariable("id") String id, 
			@RequestBody Map<String, Object> object) {
		if (this.storage.containsKey(id)) {
			throw new RuntimeException("This id is used");
		}
		storage.put(id, object);
		return object;
	}

	@RequestMapping(path = "/storage/{id}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(@PathVariable("id") String id, 
			@RequestBody Map<String, Object> object) {
		storage.put(id, object);
	}

	@RequestMapping(path = "/storage/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> get(@PathVariable("id") String id) {
		if (storage.get(id) != null)
			return storage.get(id);
		else
			throw new DataNotFoundException("could not find item by key: " + id);
	}

	@RequestMapping(path = "/storage",
			method = RequestMethod.DELETE)
	public void clearAllData() {
		storage.clear();
	}
	
	/**Retrieves all elements existing in storage**/
	@RequestMapping(path = "/storage/all",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object>[] getAllElements () {
		return this.storage
				.values()
				.stream()
				.collect(Collectors.toList())
				.toArray((Map<String, Object>[]) new Map[0]);
	}
	
	
	/**Retrieves all elements existing in storage having the ${name} value in the name field**/
	@RequestMapping(path = "/storage/byname/{name}",
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object>[] getAllSongsByName (@PathVariable("name") String name) {
		
		Map<String, Map<String, Object>> st = new TreeMap<String, Map<String,Object>>();
		for (Entry<String, Map<String, Object>> obj : storage.entrySet()) {
			if (!obj.getValue().containsKey(StorageKeys.NAME.toString().toLowerCase()))
				throw new MissingKeyException(StorageKeys.NAME.toString().toLowerCase());
			if (obj
					.getValue()
					.get(StorageKeys.NAME.toString().toLowerCase())
					.toString()
					.toLowerCase()
					.equals(name.toLowerCase())) {
				
				st.put(obj.getKey(), obj.getValue());
			}
		}
			return
					st
					.values()
					.stream()
					.collect(Collectors.toList())
					.toArray((Map<String, Object>[]) new Map[0]);
	}
	
	/**Retrieves all elements existing in storage having the ${performer} value in the performer field**/
	@RequestMapping(path = "/storage/byperformer/{performer}",
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object>[] getAllSongsByPerfomer (@PathVariable("performer") String performer) {  
		
		Map<String, Map<String, Object>> st = new TreeMap<String, Map<String,Object>>();
		for (Entry<String, Map<String, Object>> obj : storage.entrySet()) {
			if (!obj.getValue().containsKey(StorageKeys.PERFORMER.toString().toLowerCase()))
				throw new MissingKeyException(StorageKeys.PERFORMER.toString().toLowerCase());
			if (obj
					.getValue()
					.get(StorageKeys.PERFORMER.toString().toLowerCase())
					.toString()
					.toLowerCase()
					.equals(performer.toLowerCase())) {
				
				st.put(obj.getKey(), obj.getValue());
			}
		}
			return
					st
					.values()
					.stream()
					.collect(Collectors.toList())
					.toArray((Map<String, Object>[]) new Map[0]);
	}
	
	
	/**Retrieves all elements existing in storage having the ${genre} value in the genres field**/
	@RequestMapping(path = "/storage/bygenre/{genre}",
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object>[] getAllSongsByGenre (@PathVariable("genre") String genre) {
		
		Map<String, Map<String, Object>> st = new TreeMap<String, Map<String,Object>>();
		for (Entry<String, Map<String, Object>> obj : storage.entrySet()) {
			if (!obj.getValue().containsKey(StorageKeys.GENRES.toString().toLowerCase()))
				throw new MissingKeyException(StorageKeys.GENRES.toString().toLowerCase());
			List<String> genres = (List<String>)obj.getValue()
					.get(StorageKeys.GENRES.toString().toLowerCase());
			genres = genres
					.stream()
					.map(o -> o.toLowerCase())
					.collect(Collectors.toList());
			if (genres.contains(genre.toLowerCase())) {
				st.put(obj.getKey(), obj.getValue());
			}
		}
			return
					st
					.values()
					.stream()
					.collect(Collectors.toList())
					.toArray((Map<String, Object>[]) new Map[0]);
	}


	/**This exception is prompted when an item with ${key} is not found in storage**/
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public Map<String, Object> handleError(DataNotFoundException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "Item not found";
		}
		return Collections.singletonMap("error", message);
	}
	
	/**This exception is prompted when it is impossible to retrieve certain values from storage
	 * since they don't share the same key as supplied by the user
	 */
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
	public Map<String, Object> handleError(MissingKeyException e) {
		String message = e.getMessage();
		if (message == null || message.trim().length() == 0) {
			message = "Missing key in an item";
		}
		return Collections.singletonMap("error", message);
	}
}

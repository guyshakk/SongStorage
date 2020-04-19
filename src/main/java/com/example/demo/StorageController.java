package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
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
		if (this.storage.containsKey(id))
			storage.put(id, object);
		else
			throw new RuntimeException("Cannot update, id doesn't exist in storage");
	}

	@RequestMapping(path = "/storage/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> get(@PathVariable("id") String id) {
		if (storage.get(id) != null)
			return storage.get(id);
		else
			throw new DataNotFoundException("Could not find item by key: " + id);
	}

	@RequestMapping(path = "/storage",
			method = RequestMethod.DELETE)
	public void clearAllData() {
		storage.clear();
	}
	
	/**Retrieves all elements existing in storage**/
	@RequestMapping(path = "/storage/all/{sortBy}/{sortOrder}/{page}/{size}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object>[] getAllElements (@PathVariable("sortBy") String sortBy,
			@PathVariable("sortOrder") String sortOrder,
			@PathVariable("page") int page,
			@PathVariable("size") int size) {
		
		return this.storage
				.values()
				.stream()
				.sorted(getComparator(sortBy, sortOrder))
				.skip(page*size)
				.limit(size)
				.collect(Collectors.toList())
				.toArray((Map<String, Object>[]) new Map[0]);
	}
	
	
	/**Retrieves all elements existing in storage having the ${name} value in the name field**/
	@RequestMapping(path = "/storage/byname/{name}/{sortBy}/{sortOrder}/{page}/{size}",
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object>[] getAllSongsByName (@PathVariable("name") String name,
			@PathVariable("sortBy") String sortBy,
			@PathVariable("sortOrder") String sortOrder,
			@PathVariable("page") int page,
			@PathVariable("size") int size) {
		
		return this.storage
				.values()
				.stream()
				.filter(m->m.containsKey(StorageKeys.NAME.toString().toLowerCase()))
				.filter(m->(m.get(StorageKeys.NAME.toString().toLowerCase()).toString().toLowerCase().equals(name.toLowerCase())))
				.sorted(getComparator(sortBy, sortOrder))
				.skip(page*size)
				.limit(size).collect(Collectors.toList())
				.toArray((Map<String, Object>[]) new Map[0]);
	}
	
	/**Retrieves all elements existing in storage having the ${performer} value in the performer field**/
	@RequestMapping(path = "/storage/byperformer/{performer}/{sortBy}/{sortOrder}/{page}/{size}",
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object>[] getAllSongsByPerfomer (@PathVariable("performer") String performer,
			@PathVariable("sortBy") String sortBy,
			@PathVariable("sortOrder") String sortOrder,
			@PathVariable("page") int page,
			@PathVariable("size") int size) {
		
		return this.storage
				.values()
				.stream().filter(m->m.containsKey(StorageKeys.PERFORMER.toString().toLowerCase()))
				.filter(m->(m.get(StorageKeys.PERFORMER.toString().toLowerCase())).toString().toLowerCase().contains(performer.toLowerCase()))
				.sorted(getComparator(sortBy,sortOrder))
				.skip(page*size)
				.limit(size).collect(Collectors.toList())
				.toArray((Map<String, Object>[]) new Map[0]);
	}
	
	
	/**Retrieves all elements existing in storage having the ${genre} value in the genres field**/
	@RequestMapping(path = "/storage/bygenre/{genre}/{sortBy}/{sortOrder}/{page}/{size}",
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object>[] getAllSongsByGenre (@PathVariable("genre") String genre,
			@PathVariable("sortBy") String sortBy,
			@PathVariable("sortOrder") String sortOrder,
			@PathVariable("page") int page,
			@PathVariable("size") int size) {
		
		return this.storage
				.values()
				.stream()
				.filter(m->m.containsKey(StorageKeys.GENRES.toString().toLowerCase()))
				.filter(m->((ArrayList<String>)m.get(StorageKeys.GENRES.toString().toLowerCase())).contains(genre))
				.sorted(getComparator(sortBy, sortOrder))
				.skip(page*size)
				.limit(size).collect(Collectors.toList())
				.toArray((Map<String, Object>[]) new Map[0]);
	}
	
	/**Get the correct comparator for Song comparison by sortBy and sortOrder attributes**/
	private Comparator<Map<String, Object>> getComparator(String sortBy, String sortOrder) {
		
		//Check whether sortOrder value is asc or desc
		if (!Arrays.asList
				(SortOrder.values())
				.stream()
				.map(obj -> obj.toString().toLowerCase())
				.collect(Collectors.toList())
				.contains
				(sortOrder.toLowerCase())) {
			
			throw new UnsupportedSortOrderException(sortOrder);
		}
		return sortOrder.toLowerCase().
				equals(SortOrder.ASC.toString().toLowerCase()) ? 
						new CmpGeneral(sortBy) : 
							new CmpGeneral(sortBy).reversed();
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

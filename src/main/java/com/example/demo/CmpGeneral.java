package com.example.demo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class CmpGeneral implements Comparator<Map<String, Object>>{

	  private String orderBy;
	  
	  public CmpGeneral(String orderBy) {
		  this.orderBy=orderBy;

	  }

	@Override
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {

		
		switch (orderBy) {
		case "authors":
			List<Map<String, String>> arr1 = (List<Map<String, String>>) o1.get(orderBy);
			List<Map<String, String>> arr2 =(List<Map<String, String>>) o2.get(orderBy);
			List<String> authorsNames1 = getAuthorsNames(arr1);
			List<String> authorsNames2 = getAuthorsNames(arr2);
			
			for (int i = 0; i < Math.min(arr1.size(), arr2.size()); i++) {
				if (!authorsNames1.get(i).toLowerCase().equals(authorsNames2.get(i).toLowerCase())) {
					return (authorsNames1.get(i).toLowerCase().compareTo(authorsNames2.get(i).toLowerCase()));
				}
			}
			return (arr1.size() - arr2.size());
			
		case "genres":
			List<String>a1= ((List<String>) o1.get(orderBy)).stream()
					.sorted()
					.collect(Collectors.toList());
			
			List<String>a2= ((List<String>) o2.get(orderBy)).stream()
					.sorted()
					.collect(Collectors.toList());
			
			for (int i = 0; i < Math.min(a1.size(), a2.size()); i++) {
				if (!a1.get(i).equals(a2.get(i))) {
					return (a1.get(i).compareTo(a2.get(i)));
				}
			}
			return (a1.size() - a2.size());
			
		case "songId":
		case "name":
		case "lyrics":
		case "performer":
		case "producer":
			return (o1.get(this.orderBy).toString().toLowerCase()
					.compareTo(o2.get(this.orderBy).toString().toLowerCase()));
		case "publishedYear":
			int i1=Integer.parseInt(o1.get(this.orderBy).toString());
			int i2=Integer.parseInt(o2.get(this.orderBy).toString());
			int result=0;
			if(i1>i2)
				result=1;
			else if (i1<i2) {
				result=-1;
			}
			return result;
		default:
			throw new UnsupportedSortByException(orderBy);
		}
	}
	
	private List<String> getAuthorsNames(List<Map<String, String>> arr) {
		
		List<String> names = new ArrayList<>();
		for (Map<String, String> map : arr) {
			if (map.containsKey("name")) {
				names.add(map.get("name"));
			}
			else {
				throw new MissingKeyException("authors.name");
			}
		}
		return names
				.stream()
				.sorted()
				.collect(Collectors.toList());
	}
}
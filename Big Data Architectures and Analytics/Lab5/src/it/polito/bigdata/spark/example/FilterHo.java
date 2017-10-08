package it.polito.bigdata.spark.example;
import org.apache.spark.api.java.function.Function;

@SuppressWarnings("serial")
public class FilterHo implements Function<String, Boolean> {
	
	private String myWord;
	
	public FilterHo(String startWord) {
		myWord = startWord;
	}
	
	// Implement the call method that receives one element (one string)
	// It returns true if the element starts with the word ho.
	// Otherwise, it returns false.
	public Boolean call(String Line){
		return Line.toLowerCase().startsWith(myWord);
	}
}



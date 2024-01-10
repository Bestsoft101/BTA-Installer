package b100.utils;

import static b100.utils.Utils.*;

import java.util.ArrayList;
import java.util.List;

public class ParserCollection<E>{
	
	public final List<AbstractParser<E, ?>> parsers = new ArrayList<>();
	
	public E parse(Object object) {
		requireNonNull(object);
		
		for(AbstractParser<E, ?> parser : parsers) {
			if(parser.canParse(object)) {
				try{
					return parser.parse(object);
				}catch (Exception e) {
					throw new RuntimeException("Error parsing Object: "+object.getClass().getName()+" "+object, e);
				}
			}
		}
		throw new RuntimeException("No parser for class: "+object.getClass().getName());
	}
	
	public void add(AbstractParser<E, ?> parser) {
		parsers.add(parser);
	}
	
}

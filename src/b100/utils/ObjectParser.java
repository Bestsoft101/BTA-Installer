package b100.utils;

public class ObjectParser<F, E> extends AbstractParser<F, E>{
	
	private final Parser<F, E> parser;
	
	public ObjectParser(Class<E> type, Parser<F, E> parser) {
		super(type);
		this.parser = parser;
	}
	
	protected F parse2(E obj) {
		return parser.parse(obj);
	}
	
}

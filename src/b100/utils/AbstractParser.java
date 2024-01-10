package b100.utils;

public abstract class AbstractParser<ReturnType, ReadType> {
	
	public final Class<ReadType> clazz;
	
	public AbstractParser(Class<ReadType> clazz) {
		this.clazz = Utils.requireNonNull(clazz);
	}
	
	public final ReturnType parse(Object object) {
		return parse2(clazz.cast(object));
	}
	
	public boolean canParse(Object object) {
		return clazz.isAssignableFrom(object.getClass()) && object.getClass().isAssignableFrom(clazz);
	}
	
	protected abstract ReturnType parse2(ReadType obj);
	
}

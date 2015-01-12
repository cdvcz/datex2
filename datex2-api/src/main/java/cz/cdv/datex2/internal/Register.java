package cz.cdv.datex2.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Register<T> {

	private ArrayList<T> list = new ArrayList<>();

	public void add(T o) {
		if (o != null)
			list.add(o);
	}

	public void remove(T o) {
		if (o != null)
			list.remove(o);
	}

	public List<T> getAll() {
		return Collections.unmodifiableList(list);
	}

	public <S extends T> List<S> getAll(Class<S> clazz) {
		List<S> filtered = new ArrayList<>();
		for (T o : list) {
			if (o != null && clazz.isAssignableFrom(o.getClass()))
				filtered.add(clazz.cast(o));
		}

		return Collections.unmodifiableList(filtered);
	}

}

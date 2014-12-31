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

}

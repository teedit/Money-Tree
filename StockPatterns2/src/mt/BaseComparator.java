package mt;

import java.util.Comparator;
import mt.Base;
public class BaseComparator implements Comparator<Object> {

	public int compare(Object arg1, Object arg2) {
		Base base1 = (Base)arg1;
		Base base2 = (Base)arg2;
		if(base1.getBegin() < base2.getBegin())
			return -1;
		if(base1.getBegin() > base2.getBegin())
			return 1;
		else
			return 0;
	}
}

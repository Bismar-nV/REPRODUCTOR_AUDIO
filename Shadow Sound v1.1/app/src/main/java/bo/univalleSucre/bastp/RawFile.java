package bo.univalleSucre.bastp;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class RawFile extends Common {

	/**
	 * Returns the tags of a Raw File which is just an empty HashMap.
	 * This shall be used for raw streams with no (supported) tags.
	 */
	public HashMap getTags(RandomAccessFile s) throws IOException {
		HashMap tags = new HashMap();
		return tags;
	}
}


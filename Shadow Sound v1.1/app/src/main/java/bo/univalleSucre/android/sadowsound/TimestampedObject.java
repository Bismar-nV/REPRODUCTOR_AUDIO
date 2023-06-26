package bo.univalleSucre.android.sadowsound;

public class TimestampedObject {
	public long uptime;
	public Object object;

	/**
	 * Encapsulates given object and marks the creation timestamp
	 * in nanoseconds
	 */
	public TimestampedObject(Object object) {
		this.object = object;
		this.uptime = System.nanoTime();
	}

}

package bo.univalleSucre.android.medialibrary;

/**
 * Recibe devoluciones de llamada si cambia la biblioteca de medios
 */
public class LibraryObserver {
	/**
	 * Tipo de evento, a ser utilizado como una pista
	 * por el receptor
	 */
	public enum Type {
		SONG,          // Cambio que afecta a elementos de canciones.
		PLAYLIST,      // Cambio que afecta a listas de reproducción.
		SCAN_PROGRESS, // Información sobre un escaneo en curso.
	}
	/**
	 * Valores hint especiales.
	 */
	public class Value {
		public static final int UNKNOWN = -1;  // El ID exacto del objeto cambiado no se conoce, puede haber afectado a todos los elementos.
		public static final int OUTDATED = -2; // Los datos en caché no deben usarse ni confiarse.
	}

	/**
	 * Devuelve un nuevo objeto LibraryObserver.
	 */
	public LibraryObserver() {
	}

	/**
	 * Llamado si hubo un cambio, se espera que sea sobreescrito
	 * por el observador registrado.
	 *
	 * @param type uno de los tipos de LibraryObserver.Type
	 * @param id pista de qué ID cambió para el tipo, -1 si no se especifica.
	 * @param ongoing si se espera o no más eventos próximamente.
	 */
	public void onChange(Type type, long id, boolean ongoing) {
	}
}

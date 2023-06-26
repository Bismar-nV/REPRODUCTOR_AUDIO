package bo.univalleSucre.android.medialibrary;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.MediaStore;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MediaLibrary  {

	public static final String TABLE_SONGS                    = "songs";
	public static final String TABLE_ALBUMS                   = "albums";
	public static final String TABLE_CONTRIBUTORS             = "contributors";
	public static final String TABLE_CONTRIBUTORS_SONGS       = "contributors_songs";
	public static final String TABLE_GENRES                   = "genres";
	public static final String TABLE_GENRES_SONGS             = "genres_songs";
	public static final String TABLE_PLAYLISTS                = "playlists";
	public static final String TABLE_PLAYLISTS_SONGS          = "playlists_songs";
	public static final String VIEW_ARTISTS                   = "_artists";
	public static final String VIEW_ALBUMARTISTS              = "_albumartists";
	public static final String VIEW_COMPOSERS                 = "_composers";
	public static final String VIEW_ALBUMS_ARTISTS            = "_albums_artists";
	public static final String VIEW_SONGS_ALBUMS_ARTISTS      = "_songs_albums_artists";
	public static final String VIEW_SONGS_ALBUMS_ARTISTS_HUGE = "_songs_albums_artists_huge";
	public static final String VIEW_PLAYLISTS                 = "_playlists";
	public static final String VIEW_PLAYLISTS_SONGS           = "_playlists_songs";

	public static final int ROLE_ARTIST                   = 0;
	public static final int ROLE_COMPOSER                 = 1;
	public static final int ROLE_ALBUMARTIST              = 2;

	public static final int SONG_FLAG_OUTDATED            = (1 << 0); // entry in library should get rescanned.
	public static final int SONG_FLAG_NO_ALBUM            = (1 << 1); // file had no real album tag.
	public static final int SONG_FLAG_NO_ARTIST           = (1 << 2); // file had no real artist tag.

	public static final String PREFERENCES_FILE = "_prefs-v1.obj";

	/**
	 * Opciones utilizadas por la clase MediaScanner
	 */
	public static class Preferences implements Serializable {
		public boolean forceBastp;
		public boolean groupAlbumsByFolder;
		public ArrayList<String> mediaFolders;
		public ArrayList<String> blacklistedFolders;
		int _nativeLibraryCount;
		int _nativeLastMtime;
	}

	/**
	 * El progreso de un escaneo actualmente en curso, si hay alguno en ejecución
	 */
	public static class ScanProgress {
		public boolean isRunning;
		public String lastFile;
		public int seen;
		public int changed;
		public int total;
	}

	/**
	 * Preferencias almacenadas en caché, puede ser nulo
	 */
	private static Preferences sPreferences;

	/**
	 * Nuestra instancia de backend estático
	 */
	private volatile static MediaLibraryBackend sBackend;

	/**
	 * Una instancia del hilo de escaneo creado durante nuestra propia creación
	 */
	private static MediaScanner sScanner;

	/**
	 * El observador a llamar durante los cambios en la base de datos
	 */
	private static final ArrayList<LibraryObserver> sLibraryObservers = new ArrayList<LibraryObserver>(2);

	/**
	 * El bloqueo que utilizamos durante la creación del objeto
	 */
	private static final Object[] sWait = new Object[0];
	private static MediaLibraryBackend getBackend(Context context) {
		if (sBackend == null) {
			// -> unlikely
			synchronized(sWait) {
				if (sBackend == null) {
					sBackend = new MediaLibraryBackend(context.getApplicationContext());
					sScanner = new MediaScanner(context.getApplicationContext(), sBackend);
					sScanner.startQuickScan(50);
				}
			}
		}
		return sBackend;
	}

	/**
	 * Devuelve las preferencias del escáner
	 *
	 * @param context el contexto a utilizar
	 * @return MediaLibrary.Preferences
	 */
	public static MediaLibrary.Preferences getPreferences(Context context) {
		MediaLibrary.Preferences prefs = sPreferences;
		if (prefs == null) {
			try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(PREFERENCES_FILE))) {
				prefs = (MediaLibrary.Preferences)ois.readObject();
			} catch (Exception e) {
				Log.w("VanillaMusic", "Returning default media-library preferences due to error: "+ e);
			}
			if (prefs == null) {
				prefs = new MediaLibrary.Preferences();
				prefs.forceBastp = true; // Habilitar automáticamente para nuevas instalaciones
			}

			if (prefs.mediaFolders == null || prefs.mediaFolders.size() == 0)
				prefs.mediaFolders = discoverDefaultMediaPaths(context);

			if (prefs.blacklistedFolders == null) // permitimos que esto esté vacío, pero no nulo
				prefs.blacklistedFolders = discoverDefaultBlacklistedPaths(context);

			sPreferences = prefs; // almacenado en caché para un acceso frecuente
		}
		return prefs;
	}

	/**
	 * Devuelve las rutas de medios aproximadas para este dispositivo
	 *
	 * @return array con los directorios adivinados
	 */
	private static ArrayList<String> discoverDefaultMediaPaths(Context context) {
		HashSet<String> defaultPaths = new HashSet<>();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			// Ejecutándose en una plataforma que aplica acceso limitado.
			// Agrega el directorio de almacenamiento externo de forma predeterminada, pero también intenta adivinar buenas rutas externas.
			defaultPaths.add(Environment.getExternalStorageDirectory().getAbsolutePath());

			for (File file : context.getExternalMediaDirs()) {
				// Devuelve 'null' en algunos dispositivos Samsung.
				if (file == null)
					continue;

				defaultPaths.add(file.getAbsolutePath());
				// Verifica si tenemos acceso a un subdirectorio que contiene la carpeta 'Music'.
				while ((file = file.getParentFile()) != null) {
					File candidate = new File(file, "Music");
					if (candidate.exists() && !defaultPaths.contains(file.getAbsolutePath())) {
						defaultPaths.add(candidate.getAbsolutePath());
						break;
					}
				}
			}
		} else {
			// Intenta descubrir las rutas de los medios utilizando getExternalMediaDirs() en Android 5.x y versiones más nuevas.
			for (File file : context.getExternalMediaDirs()) {
				// Parece que ocurre en algunos dispositivos Samsung 5.x. :-(
				if (file == null)
					continue;

				String path = file.getAbsolutePath();
				int match = path.indexOf("/Android/media/"); // Desde Environment.DIR_ANDROID + Environment.DIR_MEDIA (ambos ocultos)
				if (match >= 0)
					defaultPaths.add(path.substring(0, match));
			}
		}

		// Recurre a la API antigua y algunas suposiciones si no se encontró nada todavía.
		if (defaultPaths.size() == 0) {
			// esto siempre debería existir
			defaultPaths.add(Environment.getExternalStorageDirectory().getAbsolutePath());
			// esto *puede* existir
			File sdCard = new File("/storage/sdcard1");
			if (sdCard.isDirectory())
				defaultPaths.add(sdCard.getAbsolutePath());
		}
		return new ArrayList<String>(defaultPaths);
	}

	/**
	 * Devuelve las rutas predeterminadas que deben agregarse a la lista negra
	 *
	 * @return array con la lista negra adivinada
	 */
	private static ArrayList<String> discoverDefaultBlacklistedPaths(Context context) {
		final String[] defaultBlacklistPostfix = { "Android/data", "Android/media", "Alarms", "Notifications", "Ringtones", "media/audio" };
		ArrayList<String> defaultPaths = new ArrayList<>();

		for (String path : discoverDefaultMediaPaths(context)) {
			for (int i = 0; i < defaultBlacklistPostfix.length; i++) {
				File guess = new File(path + "/" + defaultBlacklistPostfix[i]);
				if (guess.isDirectory())
					defaultPaths.add(guess.getAbsolutePath());
			}
		}
		return defaultPaths;
	}

	/**
	 * Actualiza las preferencias del escáner
	 *
	 * @param context el contexto a utilizar
	 * @param prefs las preferencias a almacenar; esto actualizará TODOS los campos, así que se supone
	 *              que primero debes llamar a getPreferences() para obtener los valores actuales
	 */
	public static void setPreferences(Context context, MediaLibrary.Preferences prefs) {
		MediaLibraryBackend backend = getBackend(context);

		try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(PREFERENCES_FILE, 0))) {
			oos.writeObject(prefs);
		} catch (Exception e) {
			Log.w("VanillaMusic", "Error al almacenar las preferencias de medios: " + e);
		}

		sPreferences = prefs;
	}

	/**
	 * Inicia un nuevo escaneo de la biblioteca
	 *
	 * @param context el contexto a utilizar
	 * @param forceFull inicia un escaneo completo/lento si es true
	 * @param drop elimina la biblioteca existente si es true
	 */
	public static void startLibraryScan(Context context, boolean forceFull, boolean drop) {
		MediaLibraryBackend backend = getBackend(context);
		if (drop) {
			sScanner.flushDatabase();
		}

		if (forceFull) {
			sScanner.startFullScan();
		} else {
			sScanner.startNormalScan();
		}
	}

	/**
	 * Detiene cualquier escaneo en curso
	 *
	 * @param context el contexto a utilizar
	 */
	public static void abortLibraryScan(Context context) {
		MediaLibraryBackend backend = getBackend(context); // también inicializa sScanner
		sScanner.abortScan();
	}

	/**
	 * Función extravagante para obtener el progreso actual del escaneo
	 *
	 * @param context el contexto a utilizar
	 * @return una descripción del progreso
	 */
	public static MediaLibrary.ScanProgress describeScanProgress(Context context) {
		MediaLibraryBackend backend = getBackend(context); // también inicializa sScanner
		return sScanner.describeScanProgress();
	}

	/**
	 * Crea una copia de la base de datos de medios en una ubicación especificada.
	 *
	 * @param context el contexto a utilizar
	 * @param dst la ruta de destino del volcado de la base de datos
	 */
	public static void createDebugDump(Context context, String dst) {
		final String src = context.getDatabasePath(MediaLibraryBackend.DATABASE_NAME).getPath();
		try {
			try (InputStream in = new FileInputStream(src)) {
				try (OutputStream out = new FileOutputStream(dst)) {
					byte[] buffer = new byte[4096];
					int len = 0;
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
				}
			}
		} catch (Exception e) {
			Log.v("VanillaMusic", "Error al realizar la copia de depuración: "+e);
		}
	}

	/**
	 * Registra un nuevo observador de la biblioteca de medios
	 *
	 * La MediaLibrary llamará a `onLibraryChanged()` si
	 * la biblioteca de medios cambia.
	 *
	 * `ongoing` se establecerá en `true` si se espera recibir
	 * más actualizaciones pronto. Un valor de `false` indica que no
	 * se está realizando ningún escaneo.
	 *
	 * @param observer el observador de contenido al que llamaremos en caso de cambios
	 */
	public static void registerLibraryObserver(LibraryObserver observer) {
		if (sLibraryObservers.contains(observer))
			throw new IllegalStateException("El observador de la biblioteca ya está registrado");

		sLibraryObservers.add(observer);
	}

	/**
	 * Desregistra un observador de la biblioteca que fue registrado previamente
	 * llamando a registerLibraryObserver().
	 *
	 * @param observer el observador de contenido a desregistrar.
	 */
	public static void unregisterLibraryObserver(LibraryObserver observer) {
		boolean removed = sLibraryObservers.remove(observer);

		if (!removed)
			throw new IllegalArgumentException("¡Este observador de la biblioteca nunca fue registrado!");
	}

	/**
	 * Notifica un cambio a todos los observadores registrados
	 *
	 * @param type el tipo de este evento.
	 * @param id el id del tipo que cambió, -1 si es desconocido
	 * @param ongoing si se espera o no más de estas actualizaciones pronto
	 */
	public static void notifyObserver(LibraryObserver.Type type, long id, boolean ongoing) {
		ArrayList<LibraryObserver> list = sLibraryObservers;
		for (int i = list.size(); --i != -1; )
			list.get(i).onChange(type, id, ongoing);
	}

	/**
	 * Realiza una consulta de medios en la base de datos y devuelve un cursor
	 *
	 * @param context el contexto a utilizar
	 * @param table la tabla a consultar, una de las constantes MediaLibrary.TABLE_*
	 * @param projection las columnas que se devolverán en esta consulta
	 * @param selection la selección (WHERE) a utilizar
	 * @param selectionArgs argumentos para la selección
	 * @param orderBy cómo se debe ordenar el resultado
	 */
	public static Cursor queryLibrary(Context context, String table, String[] projection, String selection, String[] selectionArgs, String orderBy) {
		return getBackend(context).query(false, table, projection, selection, selectionArgs, null, null, orderBy, null);
	}

	/**
	 * Elimina una sola canción de la base de datos
	 *
	 * @param context el contexto a utilizar
	 * @param id el id de la canción a eliminar
	 * @return el número de filas afectadas
	 */
	public static int removeSong(Context context, long id) {
		int rows = getBackend(context).delete(TABLE_SONGS, SongColumns._ID+"="+id, null);

		if (rows > 0) {
			getBackend(context).cleanOrphanedEntries(true);
			notifyObserver(LibraryObserver.Type.SONG, id, false);
			notifyObserver(LibraryObserver.Type.PLAYLIST, LibraryObserver.Value.UNKNOWN, false);
		}
		return rows;
	}

	/**
	 * Actualiza el conteo de reproducción o salto de una canción
	 *
	 * @param context el contexto a utilizar
	 * @param id el id de la canción a actualizar
	 */
	public static void updateSongPlayCounts(Context context, long id, boolean played) {
		final String column = played ? MediaLibrary.SongColumns.PLAYCOUNT : MediaLibrary.SongColumns.SKIPCOUNT;
		String selection = MediaLibrary.SongColumns._ID+"="+id;
		getBackend(context).execSQL("UPDATE "+MediaLibrary.TABLE_SONGS+" SET "+column+"="+column+"+1 WHERE "+selection);
	}

	/**
	 * Crea una nueva lista de reproducción vacía
	 *
	 * @param context el contexto a utilizar
	 * @param name el nombre de la nueva lista de reproducción
	 * @return long el id de la lista de reproducción creada, -1 en caso de error
	 */
	public static long createPlaylist(Context context, String name) {
		ContentValues v = new ContentValues();
		v.put(MediaLibrary.PlaylistColumns._ID, hash63(name));
		v.put(MediaLibrary.PlaylistColumns.NAME, name);
		v.put(MediaLibrary.PlaylistColumns.NAME_SORT, keyFor(name));
		long id = getBackend(context).insert(MediaLibrary.TABLE_PLAYLISTS, null, v);

		if (id != -1)
			notifyObserver(LibraryObserver.Type.PLAYLIST, id, false);
		return id;
	}

	/**
	 * Elimina una lista de reproducción y todos sus elementos hijos
	 *
	 * @param context el contexto a utilizar
	 * @param id el id de la lista de reproducción a eliminar
	 * @return boolean true si se eliminó la lista de reproducción
	 */
	public static boolean removePlaylist(Context context, long id) {
		// primero, borra todas las canciones
		removeFromPlaylist(context, MediaLibrary.PlaylistSongColumns.PLAYLIST_ID+"="+id, null);
		int rows = getBackend(context).delete(MediaLibrary.TABLE_PLAYLISTS, MediaLibrary.PlaylistColumns._ID+"="+id, null);
		boolean removed = (rows > 0);

		if (removed)
			notifyObserver(LibraryObserver.Type.PLAYLIST, id, false);
		return removed;
	}

	/**
	 * Agrega un grupo de canciones a una lista de reproducción
	 *
	 * @param context el contexto a utilizar
	 * @param playlistId el id del padre de la lista de reproducción
	 * @param ids una lista de array con los ids de las canciones a insertar
	 * @return el número de elementos agregados
	 */
	public static int addToPlaylist(Context context, long playlistId, ArrayList<Long> ids) {
		long pos = 0;
		// First we need to get the position of the last item
		String[] projection = { MediaLibrary.PlaylistSongColumns.POSITION };
		String selection = MediaLibrary.PlaylistSongColumns.PLAYLIST_ID+"="+playlistId;
		String order = MediaLibrary.PlaylistSongColumns.POSITION+" DESC";
		Cursor cursor = queryLibrary(context, MediaLibrary.TABLE_PLAYLISTS_SONGS, projection, selection, null, order);
		if (cursor.moveToFirst())
			pos = cursor.getLong(0) + 1;
		cursor.close();

		ArrayList<ContentValues> bulk = new ArrayList<>();
		for (Long id : ids) {
			if (getBackend(context).getColumnFromSongId(MediaLibrary.SongColumns.MTIME, id) == 0) // no mtime? song does not exist.
				continue;

			ContentValues v = new ContentValues();
			v.put(MediaLibrary.PlaylistSongColumns.PLAYLIST_ID, playlistId);
			v.put(MediaLibrary.PlaylistSongColumns.SONG_ID, id);
			v.put(MediaLibrary.PlaylistSongColumns.POSITION, pos);
			bulk.add(v);
			pos++;
		}
		int rows = getBackend(context).bulkInsert(MediaLibrary.TABLE_PLAYLISTS_SONGS, null, bulk);

		if (rows > 0)
			notifyObserver(LibraryObserver.Type.PLAYLIST, playlistId, false);
		return rows;
	}

	/**
	 * Elimina un conjunto de elementos de una lista de reproducción
	 *
	 * @param context el contexto a utilizar
	 * @param selection la selección para los elementos a eliminar
	 * @param selectionArgs argumentos para `selection'
	 * @return el número de filas eliminadas, -1 en caso de error
	 */
	public static int removeFromPlaylist(Context context, String selection, String[] selectionArgs) {
		// Obtener la lista de ids de listas de reproducción afectadas antes de realizar la eliminación.
		// Estos son necesarios para la notificación del observador.
		ArrayList<Long> playlists = new ArrayList<>();
		String[] projection = { "DISTINCT("+MediaLibrary.PlaylistSongColumns.PLAYLIST_ID+")" };
		Cursor cursor = queryLibrary(context, MediaLibrary.TABLE_PLAYLISTS_SONGS, projection, selection, selectionArgs, null);
		while(cursor.moveToNext()) {
			playlists.add(cursor.getLong(0));
		}
		cursor.close();

		int affected = 0;
		if (playlists.size() > 0) {
			affected = getBackend(context).delete(MediaLibrary.TABLE_PLAYLISTS_SONGS, selection, selectionArgs);
			for (long id : playlists) {
				notifyObserver(LibraryObserver.Type.PLAYLIST, id, false);
			}
		}

		return affected;
	}

	/**
	 * Renames an existing playlist
	 *
	 * @param context the context to use
	 * @param playlistId the id of the playlist to rename
	 * @param newName the new name of the playlist
	 * @return the id of the new playlist, -1 on error
	 */
	public static long renamePlaylist(Context context, long playlistId, String newName) {
		long newId = createPlaylist(context, newName);
		if (newId >= 0) {
			String selection = MediaLibrary.PlaylistSongColumns.PLAYLIST_ID+"="+playlistId;
			ContentValues v = new ContentValues();
			v.put(MediaLibrary.PlaylistSongColumns.PLAYLIST_ID, newId);
			getBackend(context).update(MediaLibrary.TABLE_PLAYLISTS_SONGS, v, selection, null);
			removePlaylist(context, playlistId);
		}

		if (newId != -1) {
			notifyObserver(LibraryObserver.Type.PLAYLIST, playlistId, false);
			notifyObserver(LibraryObserver.Type.PLAYLIST, newId, false);
		}
		return newId;
	}

	/**
	 * Moves an item in a playlist. Note: both items should be in the
	 * same playlist - 'fun things' will happen otherwise.
	 *
	 * @param context the context to use
	 * @param from the _id of the 'dragged' element
	 * @param to the _id of the 'repressed' element
	 */
	public static void movePlaylistItem(Context context, long from, long to) {
		long fromPos, toPos, playlistId;

		String[] projection = { MediaLibrary.PlaylistSongColumns.POSITION, MediaLibrary.PlaylistSongColumns.PLAYLIST_ID };
		String selection = MediaLibrary.PlaylistSongColumns._ID+"=";

		// Get playlist id and position of the 'from' item
		Cursor cursor = queryLibrary(context, MediaLibrary.TABLE_PLAYLISTS_SONGS, projection, selection+Long.toString(from), null, null);
		cursor.moveToFirst();
		fromPos = cursor.getLong(0);
		playlistId = cursor.getLong(1);
		cursor.close();

		// Get position of the target item
		cursor = queryLibrary(context, MediaLibrary.TABLE_PLAYLISTS_SONGS, projection, selection+Long.toString(to), null, null);
		cursor.moveToFirst();
		toPos = cursor.getLong(0);
		cursor.close();

		// Moving down -> We actually want to be below the target
		if (toPos > fromPos)
			toPos++;

		// shift all rows +1
		String setArg = MediaLibrary.PlaylistSongColumns.POSITION+"="+MediaLibrary.PlaylistSongColumns.POSITION+"+1";
		selection = MediaLibrary.PlaylistSongColumns.PLAYLIST_ID+"="+playlistId+" AND "+MediaLibrary.PlaylistSongColumns.POSITION+" >= "+toPos;
		getBackend(context).execSQL("UPDATE "+MediaLibrary.TABLE_PLAYLISTS_SONGS+" SET "+setArg+" WHERE "+selection);

		ContentValues v = new ContentValues();
		v.put(MediaLibrary.PlaylistSongColumns.POSITION, toPos);
		selection = MediaLibrary.PlaylistSongColumns._ID+"="+from;
		getBackend(context).update(MediaLibrary.TABLE_PLAYLISTS_SONGS, v, selection, null);

		notifyObserver(LibraryObserver.Type.PLAYLIST, playlistId, false);
	}

	/**
	 * Returns the number of songs in the music library
	 *
	 * @param context the context to use
	 * @return the number of songs
	 */
	public static int getLibrarySize(Context context) {
		int count = 0;
		Cursor cursor = queryLibrary(context, TABLE_SONGS, new String[]{"count(*)"}, null, null, null);
		if (cursor.moveToFirst())
			count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	/**
	 * Returns the 'key' of given string used for sorting and searching
	 *
	 * @param name the string to convert
	 * @return the the key of given name
	 */
	public static String keyFor(String name) {
		return MediaStore.Audio.keyFor(name);
	}

	/**
	 * Simple 63 bit hash function for strings
	 *
	 * @param str the string to hash
	 * @return a positive long
	 */
	public static long hash63(String str) {
		if (str == null)
			return 0;

		long hash = 0;
		int len = str.length();
		for (int i = 0; i < len ; i++) {
			hash = 31*hash + str.charAt(i);
		}
		return (hash < 0 ? hash*-1 : hash);
	}

	// Columns of Song entries
	public interface SongColumns {
		/**
		 * The id of this song in the database
		 */
		String _ID = "_id";
		/**
		 * The title of this song
		 */
		String TITLE = "title";
		/**
		 * The sortable title of this song
		 */
		String TITLE_SORT = "title_sort";
		/**
		 * The position in the album of this song
		 */
		String SONG_NUMBER = "song_num";
		/**
		 * The disc in a multi-disc album containing this song
		 */
		String DISC_NUMBER = "disc_num";
		/**
		 * The album where this song belongs to
		 */
		String ALBUM_ID = "album_id";
		/**
		 * The year of this song
		 */
		String YEAR = "year";
		/**
		 * How often the song was played
		 */
		String PLAYCOUNT = "playcount";
		/**
		 * How often the song was skipped
		 */
		String SKIPCOUNT = "skipcount";
		/**
		 * The duration of this song
		 */
		String DURATION = "duration";
		/**
		 * The path to the music file
		 */
		String PATH = "path";
		/**
		 * The mtime of this item
		 */
		String MTIME = "mtime";
		/**
		 * Various flags of this entry, see SONG_FLAG...
		 */
		String FLAGS = "_flags";
	}

	// Columns of Album entries
	public interface AlbumColumns {
		/**
		 * The id of this album in the database
		 */
		String _ID = SongColumns._ID;
		/**
		 * The title of this album
		 */
		String ALBUM = "album";
		/**
		 * The sortable title of this album
		 */
		String ALBUM_SORT = "album_sort";
		/**
		 * The primary contributor / artist reference for this album
		 */
		String PRIMARY_ARTIST_ID = "primary_artist_id";
		/**
		 * The year of this album
		 */
		String PRIMARY_ALBUM_YEAR = "primary_album_year";
		/**
		 * The mtime of this item
		 */
		String MTIME = "mtime";
	}

	// Columns of Contributors entries
	public interface ContributorColumns {
		/**
		 * The id of this contributor
		 */
		String _ID = SongColumns._ID;
		/**
		 * The name of this contributor
		 */
		String _CONTRIBUTOR = "_contributor";
		/**
		 * The sortable title of this contributor
		 */
		String _CONTRIBUTOR_SORT = "_contributor_sort";
		/**
		 * The mtime of this item
		 */
		String MTIME = "mtime";

		/**
		 * ONLY IN VIEWS - the artist
		 */
		String ARTIST = "artist";
		/**
		 * ONLY IN VIEWS - the artist_sort key
		 */
		String ARTIST_SORT = "artist_sort";
		/**
		 * ONLY IN VIEWS - the artist id
		 */
		String ARTIST_ID = "artist_id";

		/**
		 * ONLY IN VIEWS - the albumartist
		 */
		String ALBUMARTIST = "albumartist";
		/**
		 * ONLY IN VIEWS - the albumartist_sort key
		 */
		String ALBUMARTIST_SORT = "albumartist_sort";
		/**
		 * ONLY IN VIEWS - the albumartist id
		 */
		String ALBUMARTIST_ID = "albumartist_id";

		/**
		 * ONLY IN VIEWS - the composer
		 */
		String COMPOSER = "composer";
		/**
		 * ONLY IN VIEWS - the composer_sort key
		 */
		String COMPOSER_SORT = "composer_sort";
		/**
		 * ONLY IN VIEWS - the composer id
		 */
		String COMPOSER_ID = "composer_id";

	}

	// Songs <-> Contributor mapping
	public interface ContributorSongColumns {
		/**
		 * The role of this entry
		 */
		String ROLE = "role";
		/**
		 * the contirbutor id this maps to
		 */
		String _CONTRIBUTOR_ID = "_contributor_id";
		/**
		 * the song this maps to
		 */
		String SONG_ID = "song_id";
	}

	// Columns of Genres entries
	public interface GenreColumns {
		/**
		 * The id of this genre
		 */
		String _ID = SongColumns._ID;
		/**
		 * The name of this genre
		 */
		String _GENRE = "_genre";
		/**
		 * The sortable title of this genre
		 */
		String _GENRE_SORT = "_genre_sort";
	}

	// Songs <-> Contributor mapping
	public interface GenreSongColumns {
		/**
		 * the genre id this maps to
		 */
		String _GENRE_ID = "_genre_id";
		/**
		 * the song this maps to
		 */
		String SONG_ID = "song_id";
	}

	// Playlists
	public interface PlaylistColumns {
		/**
		 * The id of this playlist
		 */
		String _ID = SongColumns._ID;
		/**
		 * The name of this playlist
		 */
		 String NAME = "name";
		/**
		 * Sortable column for name
		 */
		String NAME_SORT = "name_sort";
	}

	// Song <-> Playlist mapping
	public interface PlaylistSongColumns {
		/**
		 * The ID of this entry
		 */
		String _ID = SongColumns._ID;
		/**
		 * The playlist this entry belongs to
		 */
		String PLAYLIST_ID = "playlist_id";
		/**
		 * The song this entry references to
		 */
		String SONG_ID = "song_id";
		/**
		 * The order attribute
		 */
		String POSITION = "position";
	}

	// Preference keys
	public interface PreferenceColumns {
		/**
		 * The key of this preference
		 */
		String KEY = "key";
		/**
		 * The value of this preference
		 */
		String VALUE = "value";
	}
}

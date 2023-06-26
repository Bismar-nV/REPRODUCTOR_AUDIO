package bo.univalleSucre.android.sadowsound;

import bo.univalleSucre.android.medialibrary.MediaLibrary;
import bo.univalleSucre.android.medialibrary.LibraryObserver;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.ComponentName;


@TargetApi(21)
public class ScheduledLibraryUpdate extends JobService {
	/**
	 * The unique job id of this package - do not change
	 * as we would otherwise end up with multiple scheduled
	 * jobs (until the next reboot happens)
	 */
	private final static int JOB_ID_UPDATE = 9;
	/**
	 * The job parameters of the currently running job
	 */
	private JobParameters mJobParams;


	/**
	 * Schedules a new media library scan job.
	 *
	 * @return true if job was scheduled, false otherwise
	 */
	public static boolean scheduleUpdate(Context context) {
		JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

		ComponentName componentName = new ComponentName(context, ScheduledLibraryUpdate.class);
		JobInfo job = new JobInfo.Builder(JOB_ID_UPDATE, componentName)
			.setRequiresCharging(true)
			.setRequiresDeviceIdle(true)
			.setPeriodic(3600000 * 32) // run at most every ~32 hours
			.build();

		for (JobInfo pj : scheduler.getAllPendingJobs()) {
			if (jobsEqual(pj, job)) {
				return false;
			}
		}

		scheduler.schedule(job);
		return true;
	}

	/**
	 * Compare two jobs, returns `true' if the values we care about
	 * are equal
	 *
	 * @param a the first job to compare
	 * @param b the second job to compare
	 * @return true if `a' and `b' are equal jobs
	 */
	private static boolean jobsEqual(JobInfo a, JobInfo b) {
		return (a.getId() == b.getId() &&
		        a.getIntervalMillis() == b.getIntervalMillis() &&
		        a.isRequireCharging() == b.isRequireCharging() &&
		        a.isRequireDeviceIdle() == b.isRequireDeviceIdle() &&
		        a.isPeriodic() == b.isPeriodic());
	}

	/**
	 * Called by the scheduler to launch the job
	 *
	 * @param params the parameters of this job
	 * @return true if the job has been launched
	 */
	@Override
	public boolean onStartJob(JobParameters params) {
		if (params.getJobId() != JOB_ID_UPDATE)
			return false; // orphaned job, do not start

		final boolean fullScan = (Math.random() > 0.7);

		mJobParams = params;
		MediaLibrary.registerLibraryObserver(mObserver);
		MediaLibrary.startLibraryScan(this, fullScan, false);

		return true;
	}

	/**
	 * Called by the scheduler to abort the job
	 *
	 * @param params the parameters of the job to abort
	 * @return false as we do not want to get backed-off
	 */
	@Override
	public boolean onStopJob(JobParameters params) {
		finalizeScan();
		return false;
	}

	/**
	 * Aborts a running scan job
	 */
	private void finalizeScan() {
		MediaLibrary.unregisterLibraryObserver(mObserver);
		MediaLibrary.abortLibraryScan(this);
		mJobParams = null;
	}

	/**
	 * The content observer registered to the media library.
	 * The observer will receive callbacks for changed songs,
	 * the last callback will have `ongoing` set to `false`,
	 * which indicates that our job completed.
	 */
	private final LibraryObserver mObserver = new LibraryObserver() {
		@Override
		public void onChange(LibraryObserver.Type type, long id, boolean ongoing) {
			if (type == LibraryObserver.Type.SONG && !ongoing) {
				jobFinished(mJobParams, false);
				finalizeScan();
			}
		}
	};
}

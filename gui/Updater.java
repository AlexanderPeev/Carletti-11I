package gui;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Peev
 * @author Ricardas Risys
 */
public class Updater implements Runnable, UpdateSubject {
	private static final Updater instance = new Updater();
	private long interval;
	private Thread thread;
	private Set<UpdateObserver> updateObservers = new HashSet<UpdateObserver>();

	public Updater() {
		this(15000);
	}

	public Updater(long interval) {
		this.interval = interval;
		this.thread = new Thread(this);
		this.thread.start();
	}

	public static Updater getInstance() {
		return instance;
	}

	public long getInterval() {
		return this.interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(this.interval);
			}
			catch (InterruptedException e) {
				return;
			}
			notifyObservers();
		}
	}

	@Override
	public void registerObserver(UpdateObserver o) {
		updateObservers.add(o);
	}

	@Override
	public void removeObserver(UpdateObserver o) {
		updateObservers.remove(o);
	}

	@Override
	public void notifyObservers() {
		for (UpdateObserver observer : updateObservers) {
			observer.update(this);
		}
	}

}

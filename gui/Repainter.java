package gui;

import java.awt.Component;

public class Repainter implements Runnable {
	private Component target;
	private long interval;
	private Thread thread;

	public Repainter(Component target) {
		this(target, 15000);
	}

	public Repainter(Component target, long interval) {
		this.target = target;
		this.interval = interval;
		this.thread = new Thread(this);
		this.thread.start();
	}

	public Component getTarget() {
		return this.target;
	}

	public void setTarget(Component target) {
		this.target = target;
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
			if (this.target != null) {
				this.target.repaint();
			}
		}
	}

}

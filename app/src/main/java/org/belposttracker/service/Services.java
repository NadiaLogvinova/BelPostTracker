package org.belposttracker.service;

import android.content.Context;


public class Services {

	private static Services instance = new Services();

	private boolean initialized = false;

	private Context context;

	public static Services getInstance() {
		return instance;
	}

	private Services() {
	}

	public synchronized void init(Context context) {
		if (this.initialized) {
			return;
		}

		this.context = context;
		this.initialized = true;
	}

	DbService buildDbService() {
		return new DbService(this.context);
	}

	ParcelService buildParcelService() {
		return new ParcelService();
	}

	StatusService buildStatusService() {
		return new StatusService();
	}

	public void close() {
		DbService.releaseInstance();
		ParcelService.releaseInstance();
		StatusService.releaseInstance();
	}

}

package gov.nist.healthcare.iz.darq.adf.module.sqlite.model;

public class ProcessingCount {
	public int unreadPatients = 0;
	public int unreadVaccinations = 0;
	public int nbVaccinations = 0;
	public int nbPatients = 0;
	public int maxVaccination = 0;
	public int nbProviders = 0;
	public int minVaccination = 99999;
	public int historical = 0;
	public int administered = 0;

	public int getUnreadPatients() {
		return unreadPatients;
	}

	public void setUnreadPatients(int unreadPatients) {
		this.unreadPatients = unreadPatients;
	}

	public void addUnreadPatients(int unreadPatients) {
		this.unreadPatients = this.unreadPatients + unreadPatients;
	}


	public int getUnreadVaccinations() {
		return unreadVaccinations;
	}

	public void setUnreadVaccinations(int unreadVaccinations) {
		this.unreadVaccinations = unreadVaccinations;
	}

	public void addUnreadVaccinations(int unreadVaccinations) {
		this.unreadVaccinations = this.unreadVaccinations + unreadVaccinations;
	}

	public int getNbVaccinations() {
		return nbVaccinations;
	}

	public void setNbVaccinations(int nbVaccinations) {
		this.nbVaccinations = nbVaccinations;
	}

	public void addNbVaccinations(int nbVaccinations) {
		this.nbVaccinations = this.nbVaccinations + nbVaccinations;
	}

	public int getNbPatients() {
		return nbPatients;
	}

	public void setNbPatients(int nbPatients) {
		this.nbPatients = nbPatients;
	}

	public void addNbPatients(int nbPatients) {
		this.nbPatients = this.nbPatients + nbPatients;
	}

	public int getMaxVaccination() {
		return maxVaccination;
	}

	public void setMaxVaccination(int maxVaccination) {
		this.maxVaccination = maxVaccination;
	}

	public int getMinVaccination() {
		return minVaccination;
	}

	public void setMinVaccination(int minVaccination) {
		this.minVaccination = minVaccination;
	}

	public int getHistorical() {
		return historical;
	}

	public void setHistorical(int historical) {
		this.historical = historical;
	}

	public void addHistorical(int historical) {
		this.historical = this.historical + historical;
	}

	public int getAdministered() {
		return administered;
	}

	public void setAdministered(int administered) {
		this.administered = administered;
	}

	public void addAdministered(int administered) {
		this.administered = this.administered + administered;
	}

	public int getNbProviders() {
		return nbProviders;
	}

	public void setNbProviders(int nbProviders) {
		this.nbProviders = nbProviders;
	}
}

package gov.nist.healthcare.iz.darq.batch.domain;

import java.nio.file.Paths;
import java.util.Date;

import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;

public class JobData {
	
	private String id;
	private String name;
	private Date dateStarted;
	private Date dateEnded;
	private Date dateCreated;
	private Date dateLastUpdated;
	private JobStatus status;
	private Configuration configuration;
	private String mount;
	private String resultId;
	private float progress;
	private String user;
	
	public static class FileStatus {
		public boolean patients;
		public boolean vaccinations;
	}
	
	public FileStatus getFileStatus(){
		FileStatus stat = new FileStatus();
		stat.patients = this.fileStatus(EntityTypeRegistry.get("patient"));
		stat.vaccinations = this.fileStatus(EntityTypeRegistry.get("vaccination"));
		return stat;
	}
	
	public boolean fileStatus(EntityType etype){
		return Paths.get(this.mount, "data" , etype.i.file).toFile().exists();
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDateStarted() {
		return dateStarted;
	}
	public void setDateStarted(Date dateStarted) {
		this.dateStarted = dateStarted;
	}
	public Date getDateEnded() {
		return dateEnded;
	}
	public void setDateEnded(Date dateEnded) {
		this.dateEnded = dateEnded;
	}
	public JobStatus getStatus() {
		return status;
	}
	public void setStatus(JobStatus status) {
		this.status = status;
	}
	public Configuration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	public String getMount() {
		return mount;
	}
	public void setMount(String mount) {
		this.mount = mount;
	}
	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}
	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}
	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}
	

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}
	

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public String toString(){
		return "[JOB] "+id+"\n\t STATUS : "+status+"\n\t PROGRESS : "+progress*100+"%\n\t MOUNT : "+this.getMount();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobData other = (JobData) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}

package utils;

public class ActivityEntity {
	public long startTime;
	public long endTime;
	// public int activity_id;
	public int activityId;

	public ActivityEntity() {
		startTime = 0;
		endTime = 0;
		activityId = 0;
	}

	public ActivityEntity(long start, long end, int id) {
		startTime = start;
		endTime = end;
		activityId = id;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	
	public long duration(){
		return this.endTime - this.startTime;
	}

	
}

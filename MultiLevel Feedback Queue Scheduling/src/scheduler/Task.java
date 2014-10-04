package scheduler;

public class Task implements Comparable<Task> {
	
	public int burstTime;
	public int arrivalTime;
	public int deadlineTime;
	
	private int completedTime;
	
	public Task(int burstTime, int arrivalTime, int deadlineTime) {
		this.burstTime = burstTime;
		this.arrivalTime = arrivalTime;
		this.deadlineTime = deadlineTime;
	}

	@Override
	public int compareTo(Task o) {
		return burstTime - o.burstTime;
	}
	
	public int getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(int completedTime) {
		this.completedTime = completedTime;
	}

}

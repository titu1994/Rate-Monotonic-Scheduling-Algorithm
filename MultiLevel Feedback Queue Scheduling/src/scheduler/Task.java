package scheduler;

public class Task implements Comparable<Task> {
	private static int count = 0;
	private int id = 0;
	public int burstTime;
	public int arrivalTime;
	public int deadlineTime;
	
	private int completedTime;
	
	public Task(int burstTime, int arrivalTime, int deadlineTime) {
		this.burstTime = burstTime;
		this.arrivalTime = arrivalTime;
		this.deadlineTime = deadlineTime;
		id = ++count;
	}

	@Override
	public int compareTo(Task o) {
		return burstTime - o.burstTime;
	}
	
	@Override
	public boolean equals(Object obj) {
		Task t = (Task) obj;
		if(burstTime == t.burstTime && arrivalTime == t.arrivalTime && deadlineTime == t.deadlineTime)
			return true;
		else
			return false;
	}

	public int getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(int completedTime) {
		this.completedTime = completedTime;
	}

	@Override
	public String toString() {
		return "Task " + (id);
	}
	
	public String toStringWithDeadLine() {
		return "Task " + (id) + " with deadline : " + deadlineTime;
	}

}

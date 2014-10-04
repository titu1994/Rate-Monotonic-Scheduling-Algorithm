package scheduler;

import java.util.Arrays;

public class RateMonotonicScheduling {

	private double boundary;
	private double limitSummation;
	private int currentIndex;

	private int time;
	private int maxTime;

	private Task taskPool[];
	private boolean finished[];

	public RateMonotonicScheduling(Task... taskPool) {
		this.taskPool = taskPool;
		finished = new boolean[taskPool.length];

		calculateBoundary(taskPool.length);
		calculateLimitSummation(taskPool);
	}

	public void executeTasks() {
		findNextCurrent();
		for(time = 0; time < maxTime; time++) {
			if(currentIndex != -1) {
				if(--taskPool[currentIndex].burstTime == 0) {
					finished[currentIndex] = true;
					
					if(taskPool[currentIndex].deadlineTime >= time) 
						System.out.println(taskPool[currentIndex] + " finished at time " + (time+1) + " within deadline : " + taskPool[currentIndex].deadlineTime);
					else
						System.out.println(taskPool[currentIndex] + " finished at time " + (time+1) + " exceeding deadline : " + taskPool[currentIndex].deadlineTime);
					
					findNextCurrent();
				}
				else {
					System.out.println("Time " + (time+1) + ": " + taskPool[currentIndex]);
				}
			}
			else {
				System.out.println("-1 as index at time " + time);
			}
			currentIndex = searchNextExecutableTaskAtTime();
		}

	}

	private void findNextCurrent() {
		for(int i = 0; i < taskPool.length; i++) {
			if(!finished[i] && taskPool[i].arrivalTime <= time) {
				currentIndex = i;
				break;
			}
		}
	}

	private int searchNextExecutableTaskAtTime() {
		for(int i = 0; i < taskPool.length; i++) {
			if(!finished[i] && taskPool[i].arrivalTime <= time && taskPool[i].burstTime <= taskPool[currentIndex].burstTime) 
				return i;
		}
		return -1;
	}

	private void calculateBoundary(int numberOfTasks) {
		boundary = numberOfTasks * ((Math.pow(2, 1/(double)numberOfTasks)) - 1);
		System.out.println("Boundary : " + boundary);
	}

	private void calculateLimitSummation(Task tasks[]) {
		limitSummation = 0;
		Arrays.sort(tasks);

		for(int i = 0; i < tasks.length; i++) {
			if(!isSchedulable(tasks[i])) {
				System.out.println(tasks[i].toStringWithDeadLine() + " may not complete within deadline.");
			}
			maxTime += tasks[i].burstTime;
		}
	}

	private boolean isSchedulable(Task test) {
		double limitCopy = limitSummation;
		limitCopy += (test.burstTime/(double)test.deadlineTime);
		
		if(limitCopy <= boundary) {
			limitSummation = limitCopy;
			System.out.println("Limit Summation : " + limitSummation + " for " + test);
			return true;
		}
		else {
			System.out.println("Limit Summation : " + limitCopy + " for " + test +" exceeds Boundary : " + boundary);
			return false;
		}
	}

}

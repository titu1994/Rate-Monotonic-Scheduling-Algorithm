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
	private int burstTime[];
	private int arrivalTime[];

	private double tat, wt;
	private RateMonotonicSchedulerListener listener;
	private int offset;

	public RateMonotonicScheduling(Task... taskPool) {
		this.taskPool = taskPool;
		finished = new boolean[taskPool.length];

		burstTime = new int[taskPool.length];
		arrivalTime = new int[taskPool.length];

		boolean startZeroTest = false;
		for(int i=0; i<taskPool.length; i++){
			arrivalTime[i] = taskPool[i].arrivalTime;
			
			
			if(arrivalTime[i] == 0)
				startZeroTest = true;
		}
		
		if(!startZeroTest)
			compensateForZero();

		//calculateBoundary(taskPool.length);
		//calculateLimitSummation(taskPool);
	}

	private void compensateForZero() {
		int min = 65565;
		//Find Offset to bring atleast one task to start at 0
		for(int i = 0; i < taskPool.length; i++)
			if(min > taskPool[i].arrivalTime)
				min = taskPool[i].arrivalTime;
		
		//Change time factor from 0 to whatever time we virtually begin at
		time = min;
		offset = min;
		//Compensate the arrival time for the new start time
		/*for(int i = 0; i < taskPool.length; i++) {
			taskPool[i].arrivalTime -= min;
		}*/
	}

	public void executeTasks() {
		findNextCurrent();
		for(; time < maxTime + offset; time++) {
			if(currentIndex != -1) {
				if(--taskPool[currentIndex].burstTime == 0) {
					finished[currentIndex] = true;

					if(taskPool[currentIndex].deadlineTime >= time){
						System.out.println(taskPool[currentIndex] + " finished at time " + (time+1) + " within deadline : " + taskPool[currentIndex].deadlineTime);

						if(listener != null)
							listener.taskFinished(taskPool[currentIndex], burstTime[currentIndex], arrivalTime[currentIndex], (time+1), true);

						wt += (time - burstTime[currentIndex] - arrivalTime[currentIndex]);
						tat += (time - arrivalTime[currentIndex]);
					}
					else {
						System.out.println(taskPool[currentIndex] + " finished at time " + (time+1) + " exceeding deadline : " + taskPool[currentIndex].deadlineTime);

						if(listener != null)
							listener.taskFinished(taskPool[currentIndex], burstTime[currentIndex], arrivalTime[currentIndex], (time+1), false);
					}
					findNextCurrent();
				}
				else {
					if(taskPool[currentIndex].arrivalTime <= time)
						System.out.println("Time " + (time+1) + ": " + taskPool[currentIndex]);
					else {
						System.out.println("Time " + (time+1) + ": No task executed");
					}
				}
			}
			else {
				System.out.println("-1 as index at time " + (time+1));
			}
			
			currentIndex = searchNextExecutableTaskAtTime();
		}

		wt += taskPool.length;
		tat += taskPool.length;

		System.out.println("Waiting Time: " + wt);
		System.out.println("Average Waiting Time: " + (wt/taskPool.length));
		System.out.println("Turnaround Time: " + tat);
		System.out.println("Average Turnaround Time: " + (tat/taskPool.length));

		if(listener != null)
			listener.AwtAndAtat(wt, wt/taskPool.length, tat, tat/taskPool.length);

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

	public void calculateBoundary(int numberOfTasks) {
		boundary = numberOfTasks * ((Math.pow(2, 1/(double)numberOfTasks)) - 1);
		System.out.println("Boundary : " + boundary);

		if(listener != null)
			listener.taskBoudary(boundary);
	}

	public void calculateLimitSummation(Task tasks[]) {
		limitSummation = 0;
		Arrays.sort(tasks);
		boolean areAllSchedulable = true;

		for(int i=0; i<taskPool.length; i++){
			burstTime[i] = taskPool[i].burstTime;
			arrivalTime[i] = taskPool[i].arrivalTime;
		}
	
		
		for(int i = 0; i < tasks.length; i++) {
			if(!isSchedulable(tasks[i])) {
				areAllSchedulable &= true;
			}
			else {
				areAllSchedulable &= false;
			}
			maxTime += tasks[i].burstTime;
		}

		if(listener != null) {
			listener.taskLimit(limitSummation);
			listener.taskSchedulability(areAllSchedulable);
		}
	}

	private boolean isSchedulable(Task test) {
		double limitCopy = limitSummation;
		limitCopy += (test.burstTime/(double)test.deadlineTime);

		if(limitCopy <= boundary) {
			limitSummation = limitCopy;
			return true;
		}
		else {
			return false;
		}
	}

	public void setListener(RateMonotonicSchedulerListener listener) {
		this.listener = listener;
	}

	public interface RateMonotonicSchedulerListener {
		void taskFinished(Task task, int burstTime,int arrivalTime, int time, boolean withinDeadline);
		void taskBoudary(double boundary);
		void taskLimit(double limit);
		void taskSchedulability(boolean areAllSchedulable);
		void AwtAndAtat(double waitingTime, double awt, double turnaroundTime, double atat);
	}

}

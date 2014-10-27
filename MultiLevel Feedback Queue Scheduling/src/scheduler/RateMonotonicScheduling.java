package scheduler;

import java.util.ArrayList;
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

	private ArrayList<GanttChartHolder> ganttChart;
	private GanttChartHolder ganttTemp;
	private boolean ganttUpdated = false;

	public RateMonotonicScheduling(Task... taskPool) {
		this.taskPool = taskPool;
		finished = new boolean[taskPool.length];

		burstTime = new int[taskPool.length];
		arrivalTime = new int[taskPool.length];

		this.ganttChart = new ArrayList<GanttChartHolder>();

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

		if(min != 0) {
			GanttChartHolder holder = new GanttChartHolder();
			holder.beginningTime = 0;
			holder.endingTime = min;
			holder.processID = "Null";
			ganttChart.add(holder);
		}
	}

	public void executeTasks() {
		findNextCurrent();
		for(; time < maxTime + offset; time++) {
			if(currentIndex != -1) {
				if(--taskPool[currentIndex].burstTime == 0) {
					finished[currentIndex] = true;

					ganttTemp.endingTime = time + 1;
					ganttTemp.processID = "Process " + taskPool[currentIndex].getId();
					ganttChart.add(ganttTemp);
					System.out.println("Finished : " + ganttTemp);

					ganttTemp = new GanttChartHolder();
					System.out.println("Current Time : " + time);
					ganttTemp.beginningTime = time + 1;
					ganttUpdated = true;

					if(taskPool[currentIndex].deadlineTime >= time){
						System.out.println(taskPool[currentIndex] + " finished at time " + (time+1) + " within deadline : " + taskPool[currentIndex].deadlineTime);

						if(listener != null)
							listener.taskFinished(taskPool[currentIndex], burstTime[currentIndex], arrivalTime[currentIndex], (time+1), true);
					}
					else {
						System.out.println(taskPool[currentIndex] + " finished at time " + (time+1) + " exceeding deadline : " + taskPool[currentIndex].deadlineTime);

						if(listener != null)
							listener.taskFinished(taskPool[currentIndex], burstTime[currentIndex], arrivalTime[currentIndex], (time+1), false);
					}
					wt += (time - burstTime[currentIndex] - arrivalTime[currentIndex]);
					tat += (time - arrivalTime[currentIndex]);
					taskPool[currentIndex].burstTime = 65535;
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

		if(listener != null) {
			listener.AwtAndAtat(wt, wt/taskPool.length, tat, tat/taskPool.length);
			listener.ganttChartData(ganttChart);
		}

	}

	private void findNextCurrent() {
		if(!ganttUpdated) {
			ganttTemp = new GanttChartHolder();
			ganttTemp.beginningTime = time;
			ganttUpdated = false;
		}
		for(int i = 0; i < taskPool.length; i++) {
			if(!finished[i] && taskPool[i].arrivalTime <= (time)) {
				currentIndex = i;
				break;
			}
		}
	}

	private int searchNextExecutableTaskAtTime() {
		for(int i = 0; i < taskPool.length; i++) {
			if(!finished[i] && taskPool[i].arrivalTime <= (time + 1) && taskPool[i].burstTime <= taskPool[currentIndex].burstTime) {
				if(i != currentIndex) {
					if(ganttTemp.beginningTime != (time + 1)) {
						ganttTemp.endingTime = time+1;
						ganttTemp.processID = "Process " + taskPool[currentIndex].getId();
						ganttChart.add(ganttTemp);

						System.out.println("Changed : " + ganttTemp);

						ganttTemp = new GanttChartHolder();
						ganttTemp.beginningTime = time+1;
					}
				}

				return i;
			}
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
			if(isSchedulable(tasks[i])) {
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
		limitSummation = limitCopy;

		if(limitCopy <= boundary) {
			return true;
		}
		else {
			return false;
		}
	}

	public class GanttChartHolder {
		public int beginningTime;
		public int endingTime;
		public String processID;

		@Override
		public String toString() {
			return "|" + beginningTime + " - " + processID + " - " + endingTime + "|" ;
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
		void ganttChartData(ArrayList<GanttChartHolder> ganttChart);
	}

}

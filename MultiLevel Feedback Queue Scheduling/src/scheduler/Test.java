package scheduler;

public class Test {

	public static void main(String[] args) {
		Task t1 = new Task(10, 0, 20);
		Task t2 = new Task(10, 10, 30);
		Task t3 = new Task(10, 3, 30);
		
		
		RateMonotonicScheduling scheduler = new RateMonotonicScheduling(t1,t2,t3);
		scheduler.executeTasks();

	}

}

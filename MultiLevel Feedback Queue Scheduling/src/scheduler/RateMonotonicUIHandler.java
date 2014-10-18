package scheduler;

import java.util.ArrayList;

import scheduler.TaskAddUIClass.AddUIListener;

public class RateMonotonicUIHandler {
	
	private static TaskAddUIClass addUI;
	private static SchedulerResultUIClass resultUI;
	public static RateMonotonicScheduling scheduler;
	
	public static void main(String[] args) {
		addUI = new TaskAddUIClass();
		resultUI = new SchedulerResultUIClass();
		
		addUI.setVisible(true);
		
		addUI.setAddUIListener(new AddUIListener() {
			
			@Override
			public void onAddActionSubmit(ArrayList<Task> taskList) {
				addUI.setVisible(false);
				resultUI.setVisible(true);
				
				Task tasks[] = taskList.toArray(new Task[taskList.size()]);
				scheduler = new RateMonotonicScheduling(tasks);
				
				scheduler.setListener(resultUI);
				scheduler.calculateBoundary(taskList.size());
				scheduler.calculateLimitSummation(tasks);
				
				scheduler.executeTasks();
			}
		});
	}

}

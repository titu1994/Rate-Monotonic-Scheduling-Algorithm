package scheduler;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import scheduler.RateMonotonicScheduling.RateMonotonicSchedulerListener;

public class SchedulerResultUIClass extends JFrame implements RateMonotonicSchedulerListener {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<DisplayClass> displayList;
	private DataModel model;
	
	private JLabel boundaryLabel, limitLabel, wtLabel, awtLabel, atatLabel, tatLabel, areAllScheduleLabel;
	private JLabel boundaryLabelVal, limitLabelVal, wtLabelVal, awtLabelVal, atatLabelVal, tatLabelVal, areAllScheduleVal;
	private JTable table;
	
	public SchedulerResultUIClass() {
		displayList = new ArrayList<DisplayClass>();
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		setupData();
	}
	
	private void setupData() {
		
		boundaryLabel = new JLabel();
		limitLabel = new JLabel();
		wtLabel = new JLabel();
		awtLabel = new JLabel();
		tatLabel = new JLabel();
		atatLabel = new JLabel();
		areAllScheduleLabel = new JLabel();
		
		boundaryLabelVal = new JLabel();
		limitLabelVal = new JLabel();
		wtLabelVal = new JLabel();
		awtLabelVal = new JLabel();
		tatLabelVal = new JLabel();
		atatLabelVal = new JLabel();
		areAllScheduleVal = new JLabel();
		
		tableSetup();
		
		//Labels
		boundaryLabel.setText("Boudary:");
		boundaryLabel.setBounds(0, 0, 50, 50);
		getContentPane().add(boundaryLabel);
		
		limitLabel.setText("Limit:");
		limitLabel.setBounds(0, 50, 50, 50);
		getContentPane().add(limitLabel);
		
		wtLabel.setText("Waiting Time:");
		wtLabel.setBounds(0, 100, 200, 50);
		getContentPane().add(wtLabel);
		
		awtLabel.setText("Average WT:");
		awtLabel.setBounds(0, 150, 200, 50);
		getContentPane().add(awtLabel);
		
		tatLabel.setText("Turnaround Time:");
		tatLabel.setBounds(0, 200, 200, 50);
		getContentPane().add(tatLabel);
		
		atatLabel.setText("Average TAT:");
		atatLabel.setBounds(0, 250, 200, 50);
		getContentPane().add(atatLabel);
		
		//Values
		boundaryLabelVal.setText("-");
		boundaryLabelVal.setBounds(50, 0, 100, 50);
		getContentPane().add(boundaryLabelVal);
		
		limitLabelVal.setText("-");
		limitLabelVal.setBounds(50, 50, 100, 50);
		getContentPane().add(limitLabelVal);
		
		wtLabelVal.setText("-");
		wtLabelVal.setBounds(100, 100, 200, 50);
		getContentPane().add(wtLabelVal);
		
		awtLabelVal.setText("-");
		awtLabelVal.setBounds(100, 150, 200, 50);
		getContentPane().add(awtLabelVal);
		
		tatLabelVal.setText("-");
		tatLabelVal.setBounds(125, 200, 200, 50);
		getContentPane().add(tatLabelVal);
		
		atatLabelVal.setText("-");
		atatLabelVal.setBounds(100, 250, 200, 50);
		getContentPane().add(atatLabelVal);
		
		areAllScheduleLabel.setBounds(0, 300, 200, 50);
		areAllScheduleLabel.setText("Are all schedulable:");
		getContentPane().add(areAllScheduleLabel);
		
		areAllScheduleVal.setBounds(200, 300, 100, 50);
		areAllScheduleVal.setText("-");
		getContentPane().add(areAllScheduleVal);
		
	}

	private void tableSetup() {
		DisplayClass d = new DisplayClass();
		d.processID =  "Process ID";
		d.burstTime = "Burst Time";
		d.arrivalTime = "Arrival Time";
		d.deadline = "Deadline";
		d.completedOnTime = "Before/on deadline";
		d.time = "Time";
		displayList.add(d);
		
		model = new DataModel();
		table = new JTable(model);
		table.setBounds(300, 0, 800, 800);
		table.setVisible(true);
		getContentPane().add(table);
	}

	@Override
	public void taskFinished(Task task, int burstTime, int arrivalTime, int time, boolean withinDeadline) {
		DisplayClass d = new DisplayClass();
		d.processID = task.getId() + "";
		d.burstTime = burstTime + "";
		d.arrivalTime = arrivalTime + "";
		d.deadline = task.deadlineTime + "";
		d.completedOnTime = withinDeadline + "";
		d.time = time + "";
		
		displayList.add(d);
		model.fireTableDataChanged();
	}

	@Override
	public void taskBoudary(double boundary) {
		boundaryLabelVal.setText(boundary + "");
	}

	@Override
	public void taskLimit(double limit) {
		limitLabelVal.setText(limit + "");
	}

	@Override
	public void taskSchedulability(boolean areAllSchedulable) {
		areAllScheduleVal.setText(areAllSchedulable + "");
	}

	@Override
	public void AwtAndAtat(double waitingTime, double awt, double turnaroundTime, double atat) {
		wtLabelVal.setText(waitingTime + "");
		awtLabelVal.setText(awt + "");
		tatLabelVal.setText(turnaroundTime + "");
		atatLabelVal.setText(atat + "");
	}
	
	private class DisplayClass {
		String processID;
		String burstTime;
		String arrivalTime;
		String deadline;
		String time;
		String completedOnTime;
	}
	
	private class DataModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1962279468479145799L;

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public int getRowCount() {
			return displayList.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			if(col == 0) 
				return displayList.get(row).processID;
			else if(col == 1) 
				return displayList.get(row).burstTime; 
			else if(col == 2) 
				return displayList.get(row).arrivalTime; 
			else if(col == 3) 
				return displayList.get(row).deadline; 
			else if(col == 4) 
				return displayList.get(row).time; 
			else 
				return displayList.get(row).completedOnTime; 
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

}

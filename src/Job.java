import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.print.attribute.standard.MediaSize.Other;

public class Job {
	int result = 0;
	int private_key;
	int reward;
	int id;
	
	int difficulty;
	
	
	ArrayList<ArrayList<Integer>> variable_history;
	ArrayList<Integer> command_line_history;
	String[] program;//the current position of the program
	
	int XOR = 1;//start with prime number
	ArrayList<Integer> XOR_history;
	ArrayList<Integer> XORS_passed;
	
	ArrayDeque<Integer> variable_state;//the current state of the stack machine
	int current_line;
	long[] time_stamps;
	long issuing_time;
	
	
	public Job clone(){
		Job job = new Job(variable_state.clone(), program.clone(), current_line, id, difficulty);
		return job;
	}
	
	
	public Job(ArrayDeque<Integer> init_var, String[] program, int start_line, int id, int difficulty){
		variable_state = init_var;
		this.program = program;
		current_line = start_line;
		variable_history = new ArrayList<ArrayList<Integer>>();
		command_line_history = new ArrayList<Integer>();
		XOR_history = new ArrayList<Integer>();
		XORS_passed = new ArrayList<Integer>();
		this.difficulty = difficulty;
		id = (int)(Math.random()*Integer.MAX_VALUE);
		
	}
	
	
	public void iterate(){
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String command = program[current_line];
		int num_a = variable_state.pop();
		int num_b = variable_state.pop();
		if(command.equals("ADD")){
			variable_state.push(num_a + num_b);
		}else if(command.equals("SUB")){
			variable_state.push(num_b - num_a);
		}else if(command.equals("MULT")){
			variable_state.push(num_a * num_b);
		}else if(command.equals("DIV")){
			variable_state.push(num_b / num_a);
		}else if(command.equals("GOTO")){
			current_line = variable_state.pop()-1;
		}else{
			variable_state.push(num_b);
			variable_state.push(num_a);
			variable_state.push(Integer.parseInt(command));
		}
		
		XOR_history.add(XOR);
		XOR = XOR^num_a;
		
		if(XOR%difficulty == 0  && current_line != 0){
			XORS_passed.add(XOR_history.size()-1);
		}
		
		
		
		
		ArrayList<Integer> new_history = new ArrayList<Integer>();
		new_history.add(num_b);
		new_history.add(num_a);
		variable_history.add(new_history);
		
		command_line_history.add(current_line);
		current_line++;
		
	}
	
	public String command_at(int index){
		return program[command_line_history.get(index)];
	}
	
	public ArrayList<Integer> history_at(int index){
		return variable_history.get(index);
	}
	
	public Job execute(){
		time_stamps = new long[2];
		time_stamps[0] = System.currentTimeMillis();
		while(current_line < program.length){
			iterate();
		}
		time_stamps[1] = System.currentTimeMillis();
		return this;
	}
	
	public boolean equals(Object Other){
		return ((Job)Other).id == this.id;
	}
	
	

}

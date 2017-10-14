import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class Master_Node extends Node {

	ArrayList<Job> available;
	ArrayList<int[]> job_miner_pairs;
	
	private int num_jobs = 10;
	private int job_id_number = 0;
	
	public static final int NUM_CHECKS = 100;
	
	public Master_Node(int id){
		this.id = id;
		available = new ArrayList<Job>();
		job_miner_pairs = new ArrayList<int[]>();
		blockchain = new Blockchain();
	}
	
	public void poke(){
		System.out.println("I'm being poked! " + id);
	}
	
	public Job create_job(){
		String[] program = {"50","50","ADD","2","DIV"};
		
		ArrayDeque<Integer> variable_state = new ArrayDeque<Integer>();
		variable_state.push(0);
		variable_state.push(0);
		
		Job job = new Job(variable_state, program, 0, job_id_number);
		job_id_number++;
		
		return job;
	}
	
	public void run(){
		while(true){
			
			while(available.size() < num_jobs){
				available.add(create_job());
				try {sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
			try {sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	
	public Job allocate(Miner_Node miner, int job_index){
		double rand = Math.random();
		if(rand > 0.5 && available.size() > 0){
			Job job = available.get(job_index).clone();
			job.issuing_time = System.currentTimeMillis();
			int[] pair = {job_index, miner.id};
			job_miner_pairs.add(pair);
			return job;
		}else{
			return null;
		}
	}
	
	public boolean has_jobs(){
		return available.size() > 0;
	}
	
	
	public void receive(Miner_Node miner, Job job){
		int[] pair = {job.id, miner.id};
		boolean is_valid = job_miner_pairs.remove(pair);
		
		if(is_valid = true && verify_job(job)){
			update_blockchain();
			remove_job(job);
			blockchain.add(job, this, miner);
			Main.tot_jobs_done++;
		}
	}
	
	
	public void update_blockchain(){
		int chain_size = blockchain.length();
		Blockchain chain = blockchain;
		for(Master_Node m: Main.master_nodes){
			if(m.blockchain.length() > chain_size){
				chain_size = m.blockchain.length();
				chain = m.blockchain;
			}
		}
		blockchain = chain.clone();
	}
	
	public void remove_job(Job job){
		available.remove(job);
	}
	
	public boolean verify_job(Job job){
		int history_size = job.command_line_history.size();
		
		if(history_size != job.variable_history.size()){
			return false;
		}
		
		for(int i = 0; i < NUM_CHECKS; i++){
			int index = (int)(Math.random()*(history_size-1));
			String command = job.command_at(index);
			ArrayList<Integer> vars_a = job.history_at(index);
			ArrayList<Integer> vars_b = job.history_at(index + 1);
			
			if(command.equals("ADD") && (vars_b.get(1) != vars_a.get(0) + vars_a.get(1))){
				return false;
			}else if(command.equals("SUB") && (vars_b.get(1) != vars_a.get(0) - vars_a.get(1))){
				return false;
			}else if(command.equals("MULT") && (vars_b.get(1) != vars_a.get(0) * vars_a.get(1))){
				return false;
			}else if(command.equals("DIV") && (vars_b.get(1) != vars_a.get(0) / vars_a.get(1))){
				return false;
			}else if(!command.equals("ADD") && !command.equals("SUB") && !command.equals("MULT") && !command.equals("DIV") && vars_b.get(0) != Integer.parseInt(command)){
				return false;
			}
			return true;
		}
		
		//more and clever checks can be added here, in theory the entire history of operations can be reconstructed and tested for sanity.
		return true;
	}
	
	
	
}

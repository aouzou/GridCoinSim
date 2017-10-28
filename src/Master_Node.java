import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class Master_Node extends Node {

	ArrayList<Job> available;
	ArrayList<int[]> job_miner_pairs;
	
	ArrayList<Block> to_add;
	
	private int num_jobs = 10;
	private int job_id_number = 0;
	
	private int job_difficulty = 1;
	private long sample_time = 10000;
	private long prev_time = System.currentTimeMillis();
	private int prev_blockchain_length;
	
	public static final int NUM_CHECKS = 100;
	
	public Master_Node(int id){
		this.id = id;
		available = new ArrayList<Job>();
		job_miner_pairs = new ArrayList<int[]>();
		blockchain = new Blockchain();
		to_add = new ArrayList<Block>();
	}
	
	public void poke(){
		System.out.println("I'm being poked! " + id);
	}
	
	public Job create_job(){
		
		String[] program = {"50","50","ADD","2","DIV"};
		
		ArrayDeque<Integer> variable_state = new ArrayDeque<Integer>();
		variable_state.push(0);
		variable_state.push(0);
		
		Job job = new Job(variable_state, program, 0, job_id_number, job_difficulty);
		job_id_number++;
		
		return job;
	}
	
	public void update_difficulty(){
		long time = System.currentTimeMillis();
		long dt = time - prev_time;
		if(dt > sample_time){
			System.out.println("job difficulty " + job_difficulty);
			double txs = (blockchain.length() - prev_blockchain_length)/(dt/1000);
			job_difficulty = job_difficulty*(int)(txs/Main.desired_txs);
			job_difficulty = Math.max(job_difficulty, 1);
			System.out.println(txs + " and job difficulty " + job_difficulty);
			prev_blockchain_length = blockchain.length();
			prev_time = time;
		}
	}
	
	public void run(){
		while(true){
			update_difficulty();
			while(available.size() < num_jobs){
				available.add(create_job());
				try {sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
			try {sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			
			add_blocks();		
		}
	}
	
	
	public Job allocate(Miner_Node miner, int job_index){
		double rand = Math.random();
		if(rand > 0.5 && available.size() > 0){
			Job job = available.get(job_index).clone();
			job.issuing_time = System.currentTimeMillis();
			int[] pair = {job_index, miner.id};
			job_miner_pairs.ensureCapacity(job_miner_pairs.size()*2);
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
			remove_job(job);
			
			for(Integer i: job.XORS_passed){
				Block block = new Block(job, this, miner, i);
				to_add.ensureCapacity(to_add.size()*10);
				to_add.add(block);
				Main.tot_jobs_done++;
			}
			
			
		}
	}
	
	public void add_blocks(){
		update_blockchain();
		ArrayList<Block> to_be_removed = new ArrayList<Block>();
		for(int i = 0; i < to_add.size(); i++){
			Block b = to_add.get(i);
			if(!blockchain.contains(b)){
				blockchain.add(b);
			}else if(blockchain.depth_of(b) > 10){
				to_be_removed.add(b);
			}
		}
		for(Block b: to_be_removed){
			to_add.remove(b);
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
	
	
	public boolean is_correct_hash(Job job){
		if(job.XORS_passed.size() > 0){
			return true;
		}
		return false;
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
				System.out.println("Failed on add check");
				return false;
			}else if(command.equals("SUB") && (vars_b.get(1) != vars_a.get(0) - vars_a.get(1))){
				System.out.println("Failed on sub check");
				return false;
			}else if(command.equals("MULT") && (vars_b.get(1) != vars_a.get(0) * vars_a.get(1))){
				System.out.println("Failed on mult check");
				return false;
			}else if(command.equals("DIV") && (vars_b.get(1) != vars_a.get(0) / vars_a.get(1))){
				System.out.println("Failed on div check");
				return false;
			}else if(!command.equals("ADD") && !command.equals("SUB") && !command.equals("MULT") && !command.equals("DIV") && vars_b.get(1) != Integer.parseInt(command)){
				System.out.println("Failed on push to stack check, " + command + " " + vars_b.get(0));
				return false;
			}else if(job.XOR_history.size() != history_size){
				System.out.println("Failed on history size check");
				return false;
			}else if(job.XOR_history.get(index+1) != (job.XOR_history.get(index)^vars_a.get(1))){
				System.out.println("Failed on XOR history fidelity check");
				return false;
			}else{
				for(int xor_index: job.XORS_passed){
					if(job.XORS_passed.get(xor_index)%job.difficulty != 0){
						System.out.println("Failed on XORs passed check");
						return false;
					}
				}
			}
			return true;
		}
		
		//more and clever checks can be added here, in theory the entire history of operations can be reconstructed and tested for sanity.
		return true;
	}
	
	
	
}

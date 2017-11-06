import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class Master_Node extends Node {

	ArrayList<Job> available;
	ArrayList<int[]> job_miner_pairs;

	ArrayList<Block> to_add;
	
	private int num_jobs = 10;
	private int job_id_number = 0;

	private long sample_time = 10000;
	private long prev_time = System.currentTimeMillis();
	private int prev_blockchain_length;

	public static final int NUM_CHECKS = 100;

	public Master_Node(int id) {
		//Main.record.add("Master node " + id + " created at " + System.currentTimeMillis());
		this.id = id;
		available = new ArrayList<Job>();
		job_miner_pairs = new ArrayList<int[]>();
		blockchain = new Blockchain();
		to_add = new ArrayList<Block>();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Blockchain blockchain(int index_from){
		Blockchain chain = new Blockchain();
		chain = new Blockchain();
		chain.remove(0);
		for(int i = blockchain.length()-1; i < blockchain.length() && i >= 0; i++){
			chain.add(blockchain.get(i));
		}
		return chain;
	}
	
	public void poke() {
		System.out.println("I'm being poked! " + id);
	}

	public int job_difficulty() {
		return blockchain.get(blockchain.length() - 1).current_difficulty;
	}

	public Job create_job() {
		update_blockchain();
		String[] program = { "50", "50", "ADD", "2", "DIV" };

		ArrayDeque<Integer> variable_state = new ArrayDeque<Integer>();
		variable_state.push(0);
		variable_state.push(0);

		Job job = new Job(variable_state, program, 0, job_id_number, job_difficulty());
		job_id_number++;

		return job;
	}

	public void run() {
		while (true) {
			try{
			while (available.size() < num_jobs && to_add.size() < 50) {
				available.add(create_job());
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			add_blocks();
		}
		catch(java.lang.NullPointerException e){
			blockchain = new Blockchain();
		}
	}
	}

	public Job allocate(Miner_Node miner) {
		double rand = Math.random();
		if (rand > 0.5 && available.size() > 0) {
			int index = (int) (Math.random() * available.size());
			Job job = available.get(index).clone();
			job.issuing_time = System.currentTimeMillis();
			int[] pair = { job.id, miner.id };
			job_miner_pairs.ensureCapacity(job_miner_pairs.size() * 2);
			job_miner_pairs.add(pair);
			//Main.record.add("Master node " + id + " Allocated Job to: " + miner.id + " at time: " + System.currentTimeMillis());
			return job;
		} else {
			return null;
		}
	}

	public boolean has_jobs() {
		return available.size() > 0;
	}

	public int find_difficulty(long time) {

		try{
		int prev_difficulty = blockchain.get(blockchain.length() - 1).current_difficulty;
		
		
		if (blockchain.length() % 500 == 0 && blockchain.length() > 0) {
			int num_blocks = 250;
			int index = blockchain.length() - num_blocks;
			long dt = (time - blockchain.get(index).time_minted)/1000;
			
			if (dt == 0) {
				return prev_difficulty;
			}
			double rate = num_blocks / dt;

			int new_difficulty = (int) (prev_difficulty * (rate / Main.desired_txs));
			return Math.max(new_difficulty, 1);
		}
		return prev_difficulty;
		
		}catch(java.lang.NullPointerException e){
			return Main.num_miner_nodes/20+1; 
		}
		
	}

	public boolean receive(Miner_Node miner, Job job) {
		int[] pair = { job.id, miner.id };

		int is_valid = pair_isValid(pair);
		if (is_valid != -1 && verify_job(job)) {
			remove_job(job);

			for (Integer i : job.XORS_passed) {
				Block block = new Block(job, this, miner, i);
				to_add.ensureCapacity(to_add.size() * 10);
				to_add.add(block);
				Main.tot_jobs_done++;
			}
			Main.tot_tasks_done++;
			return true;
		}
		
		return false;
	}

	public void add_blocks() {
		update_blockchain();
		ArrayList<Block> to_be_removed = new ArrayList<Block>();
		for (int i = 0; i < to_add.size(); i++) {
			Block b = to_add.get(i);
			if (b != null) {
				if (!blockchain.contains(b)) {
					long time = System.currentTimeMillis();
					b.set_timeDifficulty(time, find_difficulty(time));
					blockchain.add(b);
				} else if (blockchain.depth_of(b) > 50) {// remember to set time and difficulty
					to_be_removed.add(b);
					//Main.record.add("Master node " + id + " confirms that block is added ");
				}
			} else {
				Main.blocks_dumped++;
			}
		}

		for (Block b : to_be_removed) {
			if (b != null)
				to_add.remove(b);
			else
				Main.blocks_dumped++;
		}
	}

	public void update_blockchain() {// remember to update this method with
										// validation steps.
		int chain_size = blockchain.length();
		//Main.record.add("Master node " + id + " updated blockchain at: " + System.currentTimeMillis());
		Blockchain chain = blockchain;

		if (chain_size < 110) {
			for (Master_Node m : Main.master_nodes) {
				if (m.blockchain.length() > chain_size && chain_isValid(m.blockchain)) {
					chain = m.blockchain;
					chain_size = chain.length();
				}
			}
			
			blockchain = chain.clone();
			return;
		}

		chain = new Blockchain();
		chain.remove(0);

		for (Master_Node m : Main.master_nodes) {
			if (m.blockchain.length() > blockchain.length()) {
				
				chain = new Blockchain();
				Block comparison_block = blockchain.get(blockchain.length() - 50);
				Blockchain other_chain = m.blockchain(100);
				for (int i = 0; i < other_chain.length(); i++) {
					chain.add(other_chain.get(other_chain.length() - i - 1));
					if (other_chain.get(other_chain.length() - i - 1).equals(comparison_block)) {
						if (chain_isValid(other_chain)) {
							blockchain.concatenate(50, other_chain);// if it doesn't work, check this again
							chain_size = blockchain.length();
						} else {
							break;
						}
					}
				}
				//Main.record.add("Master node " + id + " replaced own blockchain " + System.currentTimeMillis());
				blockchain = m.blockchain.clone();
			}

		}
	}

	public boolean chain_isValid(Blockchain chain) {

		for (int i = 1; i < chain.length(); i++) {
			if (!block_isValid(chain.get(i), chain.get(i-1))) {
				return false;
			}
		}

		return true;
	}

	public boolean block_isValid(Block block, Block prev_block) {
		if (block.operation == null) {
			Main.blocks_dumped++;
			//System.out.println("Dumped block due to null");
			return false;
		}
		if(!step_isValid(block.prev_variable_state, block.variable_state, block.operation, block.prev_xor,
				block.xor)){
			return false;
		}if(block.xor%block.current_difficulty != 0){
			//System.out.println("Failed on XOR difficulty check");
			return false;
		}
		/*
		if(block.time_minted < prev_block.time_minted){
			System.out.println("Failed on Time Minted: " + block.time_minted + " " + prev_block.time_minted);
			return false;
		}
		*/
		return true;
	}

	public void remove_job(Job job) {
		available.remove(job);

	}
	
	public Job get_original(Job job){
		if(available.contains(job)){
			int index = available.indexOf(job);
			return available.get(index);
		}
		return null;
	}

	public boolean is_correct_hash(Job job) {
		if (job.XORS_passed.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean verify_job(Job job) {

		int history_size = job.command_line_history.size();
		
		Job original = get_original(job);
		
		if(original == null){
			return false;
		}
		
		if(original.program.length != job.program.length){
			return false;
		}
		
		for(int i = 0; i < original.program.length; i++){
			if(!original.program[i].equals(job.program[i])){
				return false;
			}
		}

		if (history_size != job.variable_history.size()) {
			//System.out.println("Failed on variable history size check");
			return false;
		}

		for (int i = 0; i < NUM_CHECKS; i++) {
			int index = (int) (Math.random() * (history_size - 1));

			String command = job.command_at(index);
			ArrayList<Integer> vars_a = job.history_at(index);
			ArrayList<Integer> vars_b = job.history_at(index + 1);

			if (job.XOR_history.size() != history_size) {
				//System.out.println("Failed on xor history size check");
				return false;
			}

			for (int xor_index : job.XORS_passed) {
				if (job.XOR_history.get(xor_index) % job.difficulty != 0) {
					//System.out.println("Failed on XORs passed check");
					return false;
				}
			}

			if(!step_isValid(vars_a, vars_b, command, job.XOR_history.get(index), job.XOR_history.get(index + 1))){
				return false;
			}

		}

		// more and clever checks can be added here, in theory the entire
		// history of operations can be reconstructed and tested for sanity.
		return true;
	}

	public boolean step_isValid(ArrayList<Integer> vars_a, ArrayList<Integer> vars_b, String command, int xor_a,
			int xor_b) {
		try{
		if (command.equals("ADD") && (vars_b.get(1) != vars_a.get(0) + vars_a.get(1))) {
			//System.out.println("Failed on add check");
			return false;
		} else if (command.equals("SUB") && (vars_b.get(1) != vars_a.get(0) - vars_a.get(1))) {
			//System.out.println("Failed on sub check");
			return false;
		} else if (command.equals("MULT") && (vars_b.get(1) != vars_a.get(0) * vars_a.get(1))) {
			//System.out.println("Failed on mult check");
			return false;
		} else if (command.equals("DIV") && (vars_b.get(1) != vars_a.get(0) / vars_a.get(1))) {
			//System.out.println("Failed on div check");
			return false;
		} else if (!command.equals("ADD") && !command.equals("SUB") && !command.equals("MULT") && !command.equals("DIV")
				&& vars_b.get(1) != Integer.parseInt(command)) {
			//System.out.println("Failed on push to stack check, " + command + " " + vars_b.get(0));
			return false;
		} else if (xor_b != (xor_a ^ vars_a.get(1))) {
			//System.out.println("Failed on XOR history fidelity check");
			return false;
		}
		return true;
		}catch(java.lang.NumberFormatException e){
			//System.out.println("Failed due to bad programming");
			return false;
		}

	}

	public int pair_isValid(int[] pair) {
		if(job_miner_pairs == null){
			job_miner_pairs = new ArrayList<int[]>();
			return -1;
		}
		for (int i = 0; i < job_miner_pairs.size(); i++) {
			if (pair[0] == job_miner_pairs.get(i)[0] && pair[1] == job_miner_pairs.get(i)[1]) {
				job_miner_pairs.remove(i);
				return i;
			}
		}
		return -1;
	}

}

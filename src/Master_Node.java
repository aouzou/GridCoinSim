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
			while (available.size() < num_jobs) {
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
			return job;
		} else {
			return null;
		}
	}

	public boolean has_jobs() {
		return available.size() > 0;
	}

	public int find_difficulty(long time) {
		int prev_difficulty = blockchain.get(blockchain.length() - 1).current_difficulty;
		if (blockchain.length() % 2016 == 0 && blockchain.length() > 2016) {
			int index = blockchain.length() - 50;
			long dt = time - blockchain.get(index).time_minted;
			int num_blocks = Math.min(blockchain.length(), 50);

			if (dt == 0) {
				System.out.println("bugger");
				return prev_difficulty;
			}
			double rate = num_blocks / dt;

			int new_difficulty = (int) (prev_difficulty * (Main.desired_txs / rate));
			return new_difficulty;
		}
		return prev_difficulty;
	}

	public void receive(Miner_Node miner, Job job) {
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

		}
	}

	public void add_blocks() {
		update_blockchain();
		ArrayList<Block> to_be_removed = new ArrayList<Block>();
		for (int i = 0; i < to_add.size(); i++) {
			Block b = to_add.get(i);
			if (b != null) {
				long time = System.currentTimeMillis();
				b.set_timeDifficulty(time, find_difficulty(time));
				if (!blockchain.contains(b)) {
					blockchain.add(b);
				} else if (blockchain.depth_of(b) > 50) {// remember to set time
															// and difficulty
					to_be_removed.add(b);
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

		Blockchain chain = blockchain;

		if (chain_size <= 50) {
			for (Master_Node m : Main.master_nodes) {
				chain = m.blockchain;
				if (m.blockchain.length() > chain_size) {
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
				int start = m.blockchain.length() - 1;

				for (int i = 0; i < m.blockchain.length(); i++) {
					chain.add(m.blockchain.get(m.blockchain.length() - i - 1));
					if (m.blockchain.get(m.blockchain.length() - i - 1).equals(comparison_block)) {
						if (true) {
							blockchain.concatenate(50, chain);// if it doesn't work, check this again
							chain_size = blockchain.length();
						} else {
							break;
						}
					}
				}

			}

		}
	}

	public boolean chain_isValid(Blockchain chain) {

		for (int i = 1; i < chain.length(); i++) {
			if (!block_isValid(chain.get(i))) {
				return false;
			}
		}

		return true;
	}

	public boolean block_isValid(Block block) {
		if (block.operation == null) {
			Main.blocks_dumped++;
			return false;
		}
		return step_isValid(block.prev_variable_state, block.variable_state, block.operation, block.prev_xor,
				block.xor);
	}

	public void remove_job(Job job) {
		available.remove(job);

	}

	public boolean is_correct_hash(Job job) {
		if (job.XORS_passed.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean verify_job(Job job) {

		int history_size = job.command_line_history.size();

		if (history_size != job.variable_history.size()) {
			System.out.println("Failed on variable history size check");
			return false;
		}

		for (int i = 0; i < NUM_CHECKS; i++) {
			int index = (int) (Math.random() * (history_size - 1));

			String command = job.command_at(index);
			ArrayList<Integer> vars_a = job.history_at(index);
			ArrayList<Integer> vars_b = job.history_at(index + 1);

			if (job.XOR_history.size() != history_size) {
				System.out.println("Failed on xor history size check");
				return false;
			}

			for (int xor_index : job.XORS_passed) {
				if (job.XOR_history.get(xor_index) % job.difficulty != 0) {
					System.out.println("Failed on XORs passed check");
					return false;
				}
			}

			return step_isValid(vars_a, vars_b, command, job.XOR_history.get(index), job.XOR_history.get(index + 1));

		}

		// more and clever checks can be added here, in theory the entire
		// history of operations can be reconstructed and tested for sanity.
		return true;
	}

	public boolean step_isValid(ArrayList<Integer> vars_a, ArrayList<Integer> vars_b, String command, int xor_a,
			int xor_b) {

		if (command.equals("ADD") && (vars_b.get(1) != vars_a.get(0) + vars_a.get(1))) {
			System.out.println("Failed on add check");
			return false;
		} else if (command.equals("SUB") && (vars_b.get(1) != vars_a.get(0) - vars_a.get(1))) {
			System.out.println("Failed on sub check");
			return false;
		} else if (command.equals("MULT") && (vars_b.get(1) != vars_a.get(0) * vars_a.get(1))) {
			System.out.println("Failed on mult check");
			return false;
		} else if (command.equals("DIV") && (vars_b.get(1) != vars_a.get(0) / vars_a.get(1))) {
			System.out.println("Failed on div check");
			return false;
		} else if (!command.equals("ADD") && !command.equals("SUB") && !command.equals("MULT") && !command.equals("DIV")
				&& vars_b.get(1) != Integer.parseInt(command)) {
			System.out.println("Failed on push to stack check, " + command + " " + vars_b.get(0));
			return false;
		} else if (xor_b != (xor_a ^ vars_a.get(1))) {
			System.out.println("Failed on XOR history fidelity check");
			return false;
		}
		return true;

	}

	public int pair_isValid(int[] pair) {
		for (int i = 0; i < job_miner_pairs.size(); i++) {
			if (pair[0] == job_miner_pairs.get(i)[0] && pair[1] == job_miner_pairs.get(i)[1]) {
				job_miner_pairs.remove(i);
				return i;
			}
		}
		return -1;
	}

}

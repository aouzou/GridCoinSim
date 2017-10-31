import java.util.ArrayList;

public class Block {
	
	int master_id;
	int miner_id;
	int job_id;
	int reward;
	int block_number;
	int xor_index;
	int id;
	
	int xor;
	int prev_xor;
	
	ArrayList<Integer> variable_state;
	ArrayList<Integer> prev_variable_state;
	
	String operation;
	
	long time_minted;
	
	int current_difficulty;
	
	public Block(Job job, Master_Node master, Miner_Node miner, int index){
		master_id = master.id;
		miner_id = miner.id;
		job_id = job.id;
		reward = job.reward;
		xor_index = index;
		
		variable_state = (ArrayList<Integer>) job.variable_history.get(index).clone();
		prev_variable_state = (ArrayList<Integer>) job.variable_history.get(index-1).clone();
	
		xor = job.XOR_history.get(index);
		prev_xor = job.XOR_history.get(index-1);
		time_minted = System.currentTimeMillis();
		
		operation = job.command_at(job.command_line_history.get(index-1));
		id = (int)(Math.random()*Integer.MAX_VALUE);
		
	}
	
	public Block(){
	master_id = -1;
	miner_id = -1;
	block_number = 0;
	current_difficulty = 1;
	time_minted = System.currentTimeMillis();
	}
	
	public void set_timeDifficulty(long time, int difficulty){
		current_difficulty = difficulty;
		time_minted = time;
	}
	
	public boolean equals(Object other){
		Block other_block = (Block)other;
		
		if(other_block.master_id != master_id){
			return false;
		}if(other_block.miner_id != miner_id){
			return false;
		}if(other_block.xor_index != xor_index){
			return false;
		}if(other_block.job_id != job_id){
			return false;
		}if(other_block.id != id){
			return false;
		}
		return true;
	}
	
	
	
}

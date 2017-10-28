import java.util.ArrayList;

public class Block {

	int master_id;
	int miner_id;
	int job_id;
	int reward;
	int block_number;
	int xor_index;
	
	public Block(Job job, Master_Node master, Miner_Node miner, int index){
		master_id = master.id;
		miner_id = miner.id;
		job_id = job.id;
		reward = job.reward;
		xor_index = index;
	}
	
	
	
}

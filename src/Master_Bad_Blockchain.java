import java.util.ArrayList;

public class Master_Bad_Blockchain extends Master_Node{

	public Master_Bad_Blockchain(int id){
		super(id);
	}
	
	
	public boolean receive(Miner_Node miner, Job job) {
		int[] pair = { job.id, miner.id };

		int is_valid = pair_isValid(pair);
		if (is_valid != -1 && verify_job(job)) {
			remove_job(job);

			for (Integer i : job.XORS_passed) {
				Block block = new Block(job, this, miner, i);
				Main.Wrong_Blocks_Made++;
				corrupt_block(block);
				to_add.ensureCapacity(to_add.size() * 10);
				to_add.add(block);
			}
			return true;
		}
		return false;
	}
	
	public void corrupt_block(Block b){
		double rand = Math.random();
		//corrupt XOR, prev XOR
		if(rand < 0.375){
			b.xor = (int)(Math.random()*Integer.MAX_VALUE);
		}else if(rand < 0.5){
			b.prev_xor = (int)(Math.random()*Integer.MAX_VALUE);
		}
		//corrupt Variable State, prev variable state
		else if(rand < 0.625){
			int index = (int)(Math.random()*2);
			b.variable_state.set(index, (int)(Math.random()*Integer.MAX_VALUE));
		}else if(rand < 0.75){
			int index = (int)(Math.random()*2);
			b.prev_variable_state.set(index, (int)(Math.random()*Integer.MAX_VALUE));
		}
		//corrupt command
		else{
			b.operation = "Mistake Not My Current State Of Joshing Gentle Peevishness For The Awesome And Terrible Majesty Of The Towering Seas Of Ire That Are Themselves The Mere Milquetoast Shallows Fringing My Vast Oceans Of Wrath";
		}
		
	}
	
}

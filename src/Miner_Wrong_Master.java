import java.util.ArrayList;

public class Miner_Wrong_Master extends Miner_Node{

	
	public Miner_Wrong_Master(int id){
		super(id);
	}
	
	public void submit(Master_Node master, int job_index){
		Master_Node new_master = random_master();
		while(new_master.equals(master)){
			new_master = random_master();
		}
		Job job = jobs_done.get(job_index);
		if(new_master.receive(this, job)){
			Main.Wrong_Masters_Passed++;
		}else{
			Main.Wrong_Masters_Caught++;
		}
		jobs_done.remove(job);
	}
	
	public Master_Node random_master(){
		return Main.master_nodes.get((int) (Math.random()*Main.master_nodes.size()));
	}
	
}

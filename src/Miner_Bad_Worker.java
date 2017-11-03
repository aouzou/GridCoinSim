import java.util.ArrayList;

public class Miner_Bad_Worker extends Miner_Node{

	public Miner_Bad_Worker(int id){
		super(id);
	}

	
	public void corrupt_job(Job job){
		
		for(ArrayList<Integer> line: job.variable_history){
			if(Math.random() > 0.5){
				line.set(1, (int)(Math.random()*Integer.MAX_VALUE));
			}else{
				line.set(0, (int)(Math.random()*Integer.MAX_VALUE));
			}
		}
		
	}
	
	public void submit(Master_Node master, int job_index){
		
		Job job = jobs_done.get(job_index);
		corrupt_job(job);
		master.receive(this, job);
		jobs_done.remove(job);
		
		if(master.receive(this, job)){
			Main.Wrong_Workers_Passed++;
		}else{
			Main.Wrong_Workers_Caught++;
		}
	}	
	
}

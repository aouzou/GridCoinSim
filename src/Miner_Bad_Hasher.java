import java.util.ArrayList;

public class Miner_Bad_Hasher extends Miner_Node{

	public Miner_Bad_Hasher(int id){
		super(id);
	}

	
	public void execute_job(int job_index){
		Job job = jobs_to_do.get(job_index).execute();
		corrupt_job(job);
		jobs_to_do.remove(job);
		jobs_done.add(job);
		try {
			sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void corrupt_job(Job job){
		
		for(int i = 0; i < job.XOR_history.size(); i++){
			job.XOR_history.set(i, (int)(Math.random()*Integer.MAX_VALUE));
		}

	}
	
	
	public void submit(Master_Node master, int job_index){
		Job job = jobs_done.get(job_index);
		corrupt_job(job);
		master.receive(this, job);
		jobs_done.remove(job);
		if(master.receive(this, job)){
			Main.Wrong_Hashers_Passed++;
		}else{
			Main.Wrong_Hashers_Caught++;
		}
	}
	
	
}

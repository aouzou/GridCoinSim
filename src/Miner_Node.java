import java.util.ArrayList;
import java.util.Set;

public class Miner_Node extends Node{

	ArrayList<Job> jobs_to_do;
	ArrayList<Job> jobs_done;
	private int job_difficulty;
	
	
	public Miner_Node(int id){
		this.id = id;
		jobs_to_do = new ArrayList<Job>();
		jobs_done = new ArrayList<Job>();
	}
	

	
	public void run(){
		while(true){
			Master_Node master = Main.master_nodes.get((int)(Math.random()*Main.num_master_nodes));
			request_job(master);
			while(jobs_to_do.size() > 0){
				execute_job(0, master);
				submit(master, 0);
			}
			
		}
	}
	
	public void request_job(Master_Node master){
		if(master.has_jobs()){
			Job job = master.allocate(this);
			if(job != null){
				jobs_to_do.add(job);
			}else{
				return;
			}
		}
		
	}
	
	public void set_difficulty(Master_Node master, Job job){
		int difficulty = master.job_difficulty();
		job.difficulty = difficulty;
		
	}
	
	public void execute_job(int job_index, Master_Node master){
		
		try {
			sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		set_difficulty(master, jobs_to_do.get(job_index));
		Job job = jobs_to_do.get(job_index).execute();
		jobs_to_do.remove(job);
		jobs_done.add(0,job);
		
	}
	
	public void submit(Master_Node master, int job_index){
		Job job = jobs_done.get(job_index);
		master.receive(this, job);
		jobs_done.remove(job);
	}
	
}

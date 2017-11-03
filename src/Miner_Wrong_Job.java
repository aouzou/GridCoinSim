
public class Miner_Wrong_Job extends Miner_Node{
	
	public Miner_Wrong_Job(int id){
		super(id);
	}
	
	public void request_job(Master_Node master){
		if(master.has_jobs()){
			Job job = master.allocate(this);
			if(job != null){
				corrupt_job(job);
				jobs_to_do.add(job);
			}else{
				return;
			}
		}
		
	}
	
	public void submit(Master_Node master, int job_index){
		Job job = jobs_done.get(job_index);
		master.receive(this, job);
		jobs_done.remove(job);		
		if(master.receive(this, job)){
			Main.Wrong_Jobs_Passed++;
		}else{
			Main.Wrong_Jobs_Caught++;
		}
	}	
	
	public void corrupt_job(Job job){
		String[] program = { "40", "20", "SUB", "2", "DIV" };
		job.program = program;
	}

}

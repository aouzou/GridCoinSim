import java.util.ArrayList;

public class Blockchain {
	
	private ArrayList<Block> chain;
	
	public Blockchain(){
		chain = new ArrayList<Block>();
		chain.add(new Block());
		chain.get(0).set_timeDifficulty(0,  Main.num_miner_nodes/20 + 1);
	}
	
	public void remove(int index){
		chain.remove(index);
	}
	
	public Blockchain(ArrayList<Block> chain){
		this.chain = chain;
	}
	
	public void concatenate(int index, Blockchain other){
		for(int i = 0; i < index; i++){
			chain.remove(chain.size()-1);
		}
		for(int i = 0; i < other.length(); i++){
			chain.add(other.get(i));
		}
	}
	
	public Blockchain clone(){
		return new Blockchain((ArrayList<Block>) chain.clone());
		
	}
	
	//when a job is done NOT when it is offered, it is added to the block chain. The job is officially over and payment is given. 
	public void add(Job job, Master_Node master, Miner_Node miner, int index){
		chain.add(new Block(job, master, miner, index));
	}
	
	public void add(Block b){
		chain.add(b);
	}
	
	public int depth_of(Block b){
		int index = chain.lastIndexOf(b);
		return chain.size()-index;
	}
	
	public Block get(int index){
		return chain.get(index);
	}
	
	public boolean contains(Block b){
		return chain.contains(b);
	}
	
	public void report(int id){
		System.out.println("Blockchain length on Node: " + id + " is: " + chain.size());
	}
	/*
	public void report(int id){
		System.out.println("");
		System.out.println("==================================");
		for(Block b: chain){
			if(b != null) System.out.println("Block Master is: " + b.master_id);
		}
	}
	*/
	
	public int length(){
		if(chain == null){
			return 0;
		}
		return chain.size();
	}
	
	
}

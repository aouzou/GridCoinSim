import java.util.ArrayList;

public class Blockchain {
	
	private ArrayList<Block> chain;
	public int length = 0;
	
	public Blockchain(){
		chain = new ArrayList<Block>();
		chain.add(new Block());
		length = 1;
	}
	
	public Blockchain(ArrayList<Block> chain){
		this.chain = chain;
		this.length = chain.size();
	}
	
	public Blockchain clone(){
		return new Blockchain((ArrayList<Block>) chain.clone());
		
	}
	
	//when a job is done NOT when it is offered, it is added to the block chain. The job is officially over and payment is given. 
	public void add(Job job, Master_Node master, Miner_Node miner, int index){
		chain.add(new Block(job, master, miner, index));
		length++;
	}
	
	public void add(Block b){
		chain.add(b);
		length++;
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
		return length;
	}
	
	
}

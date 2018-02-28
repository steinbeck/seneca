package seneca.structgen.deterministic;

import seneca.structgen.StructureGenerator;
import seneca.structgen.StructureGeneratorResult;
import seneca.structgen.StructureGeneratorStatus;
import seneca.structgen.annealinglog.CommonAnnealingLog;

public class LucyDeterministicGenerator extends StructureGenerator
{
	
	private Thread structGenThread = null;

	public LucyDeterministicGenerator() {
		// TODO Auto-generated constructor stub
	}

	public void execute()
	{
		System.out.println("und los gehts");
	}
	
    public void run() {

        Thread.currentThread();
        try {
            execute();

        } catch (Exception exc) {
            exc.printStackTrace();
        }
        structGenThread = null;

    }
    
    public Object getStatus() throws java.io.IOException {
        StructureGeneratorStatus sgs = new StructureGeneratorStatus();
        //TODO Must implement this method
        return sgs;
    }

	public StructureGeneratorResult call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

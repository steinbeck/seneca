package seneca.structgen.sa.adaptive;

import seneca.structgen.annealinglog.CommonAnnealingLog;

public interface AnnealingEngineI {

    public void addTemperatureListener(TemperatureListener listener);

    public void run();

    public void setAnnealerAdapter(AnnealerAdapterI adapter);

    public void setShouldStop(boolean value);

    public boolean isFinished();

    public long getIterations();

    public CommonAnnealingLog getUpdatedAnnealingLog();
}
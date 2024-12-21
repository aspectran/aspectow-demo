package app.root.appmon.exporter;

import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.lifecycle.AbstractLifeCycle;

import java.util.List;

/**
 * <p>Created: 2024-12-18</p>
 */
public abstract class Exporter extends AbstractLifeCycle {

    public abstract String getName();

    public abstract <V extends Parameters> V getExporterInfo();

    public abstract void read(List<String> messages);

    public abstract void broadcast(String message);

}

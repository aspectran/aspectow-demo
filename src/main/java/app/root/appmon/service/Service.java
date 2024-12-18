package app.root.appmon.service;

import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.lifecycle.AbstractLifeCycle;

import java.util.List;

/**
 * <p>Created: 2024-12-18</p>
 */
public abstract class Service extends AbstractLifeCycle {

    public abstract String getName();

    public abstract <V extends Parameters> V getServiceInfo();

    public abstract void read(List<String> messages);

    public abstract void broadcast(String message);

}

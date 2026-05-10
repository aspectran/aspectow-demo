package app.appmon;

import com.aspectran.aspectow.appmon.engine.persist.counter.EventCountVO;
import com.aspectran.aspectow.appmon.engine.persist.db.mapper.EventCountMapper;
import com.aspectran.mybatis.SqlMapperProvider;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A hybrid DAO implementation that uses both local MariaDB and Supabase.
 * Writes are duplicated, and aggregated reads are served from Supabase.
 */
public class HybridEventCountDao implements EventCountMapper {

    private final SqlMapperProvider localProvider;

    private final SqlMapperProvider supabaseProvider;

    public HybridEventCountDao(SqlMapperProvider localProvider, SqlMapperProvider supabaseProvider) {
        this.localProvider = localProvider;
        this.supabaseProvider = supabaseProvider;
    }

    private EventCountMapper local() {
        return localProvider.mapper(EventCountMapper.class);
    }

    private EventCountMapper supabase() {
        return supabaseProvider.mapper(EventCountMapper.class);
    }

    @Override
    public EventCountVO getLastEventCount(String nodeId, String appId, String eventId) {
        return local().getLastEventCount(nodeId, appId, eventId);
    }

    @Override
    public void updateLastEventCount(EventCountVO eventCountVO) {
        local().updateLastEventCount(eventCountVO);
    }

    @Override
    public void insertEventCount(EventCountVO eventCountVO) {
        local().insertEventCount(eventCountVO);
    }

    @Override
    public void insertEventCountHourly(EventCountVO eventCountVO) {
        local().insertEventCountHourly(eventCountVO);
        try {
            supabase().insertEventCountHourly(eventCountVO);
        } catch (Exception e) {
            // Log and ignore Supabase errors
        }
    }

    @Override
    public List<EventCountVO> getChartData(String nodeId, String appId, String eventId, LocalDateTime dateOffset) {
        return local().getChartData(nodeId, appId, eventId, dateOffset);
    }

    @Override
    public List<EventCountVO> getChartDataByHour(String nodeId, String appId, String eventId, int zoneOffset, LocalDateTime dateOffset) {
        return supabase().getChartDataByHour(nodeId, appId, eventId, zoneOffset, dateOffset);
    }

    @Override
    public List<EventCountVO> getChartDataByDay(String nodeId, String appId, String eventId, int zoneOffset, LocalDateTime dateOffset) {
        return supabase().getChartDataByDay(nodeId, appId, eventId, zoneOffset, dateOffset);
    }

    @Override
    public List<EventCountVO> getChartDataByMonth(String nodeId, String appId, String eventId, int zoneOffset, LocalDateTime dateOffset) {
        return supabase().getChartDataByMonth(nodeId, appId, eventId, zoneOffset, dateOffset);
    }

    @Override
    public List<EventCountVO> getChartDataByYear(String nodeId, String appId, String eventId, int zoneOffset, LocalDateTime dateOffset) {
        return supabase().getChartDataByYear(nodeId, appId, eventId, zoneOffset, dateOffset);
    }

}

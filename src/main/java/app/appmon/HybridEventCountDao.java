package app.appmon;

import com.aspectran.appmon.engine.persist.counter.EventCountVO;
import com.aspectran.appmon.engine.persist.db.mapper.EventCountMapper;
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
        return localProvider.getSimpleSqlSession().getMapper(EventCountMapper.class);
    }

    private EventCountMapper supabase() {
        return supabaseProvider.getSimpleSqlSession().getMapper(EventCountMapper.class);
    }

    @Override
    public EventCountVO getLastEventCount(String domain, String instance, String event) {
        return local().getLastEventCount(domain, instance, event);
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
    public List<EventCountVO> getChartData(String domain, String instance, String event, LocalDateTime dateOffset) {
        return local().getChartData(domain, instance, event, dateOffset);
    }

    @Override
    public List<EventCountVO> getChartDataByHour(String domain, String instance, String event, int zoneOffset, LocalDateTime dateOffset) {
        return local().getChartDataByHour(domain, instance, event, zoneOffset, dateOffset);
    }

    @Override
    public List<EventCountVO> getChartDataByDay(String domain, String instance, String event, int zoneOffset, LocalDateTime dateOffset) {
        return supabase().getChartDataByDay(domain, instance, event, zoneOffset, dateOffset);
    }

    @Override
    public List<EventCountVO> getChartDataByMonth(String domain, String instance, String event, int zoneOffset, LocalDateTime dateOffset) {
        return supabase().getChartDataByMonth(domain, instance, event, zoneOffset, dateOffset);
    }

    @Override
    public List<EventCountVO> getChartDataByYear(String domain, String instance, String event, int zoneOffset, LocalDateTime dateOffset) {
        return supabase().getChartDataByYear(domain, instance, event, zoneOffset, dateOffset);
    }
}

package lt.scoutress.StatisticsApp.services.playtime;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.playtime.PlaytimeDBCodes;

public interface PlaytimeDBCodesService {

    List<PlaytimeDBCodes> findAll();

    void copyDBUsernames();

    PlaytimeDBCodes findById(int employeeId);

    PlaytimeDBCodes save(PlaytimeDBCodes dbCodes);

}

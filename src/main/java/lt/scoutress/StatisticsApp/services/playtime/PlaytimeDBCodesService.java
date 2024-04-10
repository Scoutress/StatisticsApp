package lt.scoutress.StatisticsApp.Services.playtime;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Playtime.PlaytimeDBCodes;

public interface PlaytimeDBCodesService {

    List<PlaytimeDBCodes> findAll();

    void copyDBUsernames();

    PlaytimeDBCodes findById(int employeeId);

    PlaytimeDBCodes save(PlaytimeDBCodes dbCodes);

}

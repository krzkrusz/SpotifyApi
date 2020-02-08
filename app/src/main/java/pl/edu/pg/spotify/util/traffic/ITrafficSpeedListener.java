package pl.edu.pg.spotify.util.traffic;

public interface ITrafficSpeedListener {

    void onTrafficSpeedMeasured(double upStream, double downStream);
}
package de.hsmainz.gi.beaconrangeevaluation.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author KekS
 */
public class BeaconLog implements Serializable {
    private AndroidModel            model;
    private List<BeaconLogObject>   loggedBeacons;
    private Orientation             averageOrientation;
    private double                  distance;

    /**
     * @return the model
     */
    public AndroidModel getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(AndroidModel model) {
        this.model = model;
    }

    /**
     * @return the loggedBeacons
     */
    public List<BeaconLogObject> getLoggedBeacons() {
        return loggedBeacons;
    }

    /**
     * @param loggedBeacons the loggedBeacons to set
     */
    public void setLoggedBeacons(List<BeaconLogObject> loggedBeacons) {
        this.loggedBeacons = loggedBeacons;
    }

    /**
     * @return the averageOrientation
     */
    public Orientation getAverageOrientation() {
        return averageOrientation;
    }

    /**
     * @param averageOrientation the averageOrientation to set
     */
    public void setAverageOrientation(Orientation averageOrientation) {
        this.averageOrientation = averageOrientation;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public static class AndroidModel {
        private String mManufacturer;
        private String mModel;
        private String mBuildNumber;
        private String mVersion;

        /**
         * @return the mManufacturer
         */
        public String getmManufacturer() {
            return mManufacturer;
        }

        /**
         * @param mManufacturer the mManufacturer to set
         */
        public void setmManufacturer(String mManufacturer) {
            this.mManufacturer = mManufacturer;
        }

        /**
         * @return the mModel
         */
        public String getmModel() {
            return mModel;
        }

        /**
         * @param mModel the mModel to set
         */
        public void setmModel(String mModel) {
            this.mModel = mModel;
        }

        /**
         * @return the mBuildNumber
         */
        public String getmBuildNumber() {
            return mBuildNumber;
        }

        /**
         * @param mBuildNumber the mBuildNumber to set
         */
        public void setmBuildNumber(String mBuildNumber) {
            this.mBuildNumber = mBuildNumber;
        }

        /**
         * @return the mVersion
         */
        public String getmVersion() {
            return mVersion;
        }

        /**
         * @param mVersion the mVersion to set
         */
        public void setmVersion(String mVersion) {
            this.mVersion = mVersion;
        }
    }

    public static class Orientation {
        /** rotation around Z axis */ private double azimuth = 0.0;
        /** rotation around Y axis */ private double pitch = 0.0;
        /** rotation around X axis */ private double roll = 0.0;

        /**
         * @return the azimuth
         */
        public double getAzimuth() {
            return azimuth;
        }

        /**
         * @param azimuth the azimuth to set
         */
        public void setAzimuth(double azimuth) {
            this.azimuth = azimuth;
        }

        /**
         * @return the pitch
         */
        public double getPitch() {
            return pitch;
        }

        /**
         * @param pitch the pitch to set
         */
        public void setPitch(double pitch) {
            this.pitch = pitch;
        }

        /**
         * @return the roll
         */
        public double getRoll() {
            return roll;
        }

        /**
         * @param roll the roll to set
         */
        public void setRoll(double roll) {
            this.roll = roll;
        }
    }
}

package de.hsmainz.gi.beaconrangeevaluation.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * *LIGHT* version of the Android-
 * Class to (de-)serialize logged Beacon Measurements
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 05.11.2014.
 */
public class BeaconLogObject implements Serializable,
                                        Comparable<BeaconLogObject> {
    
    private static final long serialVersionUID = 05_11_2014L;

    private Identifier        beaconId;
    private double            distance;
    private List<Measurement> measurements;
    private double            averageDistance;


    /**
     * Default Constructor for a BeaconLogObject
     *
     * @param   uuid        the beacon's uuid
     * @param   major       the beacon's major version
     * @param   minor       the beacon's minor version
     * @param   distance    the nominal distance of this measurement
     */
    public BeaconLogObject(
        String uuid,
        String major,
        String minor,
        int distance
    ) {
        this(new Identifier(uuid, major, minor), distance);
    }


    /**
     * Default Constructor for a BeaconLogObject
     *
     * @param   beaconId    the beacon identified by this {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Identifier}
     * @param   distance    the nominal distance of this measurement
     */
    public BeaconLogObject(
            Identifier beaconId,
            double distance
    ) {
        this.beaconId = beaconId;
        this.distance = distance;
        this.measurements = new ArrayList<>();
    }

    /**
     * Add a {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement} to this
     * {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject}s list
     * {@link #measurements} by generating it internally through it's Constructor
     * {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement#Measurement(long, int, int, double)}
     * by chaining through {@link #addMeasurement(long, int, int, double)} at the current time
     * ({@link System#currentTimeMillis()})
     *
     * @param   rssi            received signal strength indicator
     * @param   txPower         transmit power
     * @param   calcDistance    calculated distance
     */
    public void addMeasurement(int rssi, int txPower, double calcDistance) {
        addMeasurement(new Measurement(System.currentTimeMillis(), rssi, txPower, calcDistance));
    }

    /**
     * Add a {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement} to this
     * {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject}s list
     * {@link #measurements} by generating it internally through it's Constructor
     * {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement#Measurement(long, int, int, double)}
     *
     * @param   timestamp       timestamp of the Measurement
     * @param   rssi            received signal strength indicator
     * @param   txPower         transmit power
     * @param   calcDistance    calculated distance
     */
    public void addMeasurement(long timestamp, int rssi, int txPower, double calcDistance) {
        addMeasurement(new Measurement(timestamp, rssi, txPower, calcDistance));
    }


    /**
     * Add a {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement} to this
     * {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject}s list
     *
     * @param   measurement     a {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement}
     */
    public void addMeasurement(Measurement measurement) {
        getMeasurements().add(measurement);
    }


    /**
     * Getter for {@link #distance}
     *
     * @return  the fixed distance this logging happend at
     */
    public double getDistance() {
        return distance;
    }


    /**
     * Getter for {@link #measurements}
     *
     * @return  a {@link java.util.List} of all {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement}s so far
     */
    public List<Measurement> getMeasurements() {
        return measurements;
    }


    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param   another     the object to compare to this instance.
     * @return  a negative integer if this instance is less than {@code another};
     *          a positive integer if this instance is greater than
     *          {@code another}; 0 if this instance has the same order as
     *          {@code another}.
     * @throws  ClassCastException  if {@code another} cannot be converted into something
     *                              comparable to {@code this} instance.
     */
    @Override
    public int compareTo(BeaconLogObject another) {
        if (this.beaconId.compareTo(another.beaconId) != 0)
            return this.beaconId.compareTo(another.beaconId);
        return (int) Math.round(this.distance - another.getDistance());
    }


    /**
     * String representation for the UI.
     *
     * @return  String representation of this
     */
    @Override
    public String toString() {
        if (measurements.size() > 0) {
            return this.beaconId.toString() + " @ " + distance + "m: \u2200" + getAverageDistance() + "m (" + measurements.size() + ")";
        }
        return this.beaconId.toString() + " @ " + distance + "m.";
    }

    /**
     * String representation for the id consisting of {@link #major} &amp; {@link #minor}.
     *
     * @return  String representation of this
     */
    public String getID(){
        return  this.beaconId.major + "/" + this.beaconId.minor;
    }

    /**
     * Return an identifier object.
     *
     * @return  Identifier object
     */
    public Identifier getIdentifier(){
        return  this.beaconId;
    }

    /**
     * overridden equals method
     *
     * @param   other   some other object
     * @return  whether or not this object is equal to another object
     */
    @Override
    public boolean equals(Object other) {
        try {
            return this.compareTo((BeaconLogObject) other) == 0;
        } catch(Exception ex) {
            return false;
        }
    }

    /**
     * overridden hashCode method
     *
     * @return  the {@link Object#hashCode()} for this
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.beaconId.hashCode();
        hash = 79 * hash + Double.valueOf(distance).hashCode();
        return hash;
    }

    /**
     * Get the average distance over all {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement}s
     * saved in the {@link #measurements} list.
     *
     * @return  average distance
     */
    public double getAverageDistance() {
        //return measurements.parallelStream().mapToDouble(Measurement::getCalcDistance).average().getAsDouble();
        double dist = 0.0;
        for (Measurement m : measurements) {
            dist += m.getCalcDistance();
        }
        averageDistance = dist / measurements.size();
        return averageDistance;
    }

    /**
     * Signal you're done collecting {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject.Measurement}s
     * so {@link #averageDistance} is calculated and returned.
     *
     * @return the {@link #averageDistance}
     */
    public double done() {
        return getAverageDistance();
    }




/*=======================================================
         ___    _            _   _  __ _
        |_ _|__| | ___ _ __ | |_(_)/ _(_) ___ _ __
         | |/ _` |/ _ \ '_ \| __| | |_| |/ _ \ '__|
         | | (_| |  __/ | | | |_| |  _| |  __/ |
        |___\__,_|\___|_| |_|\__|_|_| |_|\___|_|
 =======================================================*/
    /**
     * Uniquely identified Beacon.
     *
     * @author  KekS (mailto:keks@keksfabrik.eu), 22.10.2014.
     */
    public static class Identifier implements Comparable<Identifier> {

        private String uuid;
        private String major;
        private String minor;


        /**
         * Constructor for an individually defined Beacon.
         *
         * @param   uuid    the beacon's uuid
         * @param   major   the beacon's major version
         * @param   minor   the beacon's minor version
         */
        public Identifier(String uuid, String major, String minor) {
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
        }

        /**
         * Getter for {@link #uuid}
         *
         * @return  the universally unique identifier
         */
        public String getUuid() {
            return uuid;
        }

        /**
         * Getter for {@link #major}
         *
         * @return  the major version
         */
        public String getMajor() {
            return major;
        }

        /**
         * Getter for {@link #minor}
         *
         * @return  the major
         */
        public String getMinor() {
            return minor;
        }

        /**
         * Compares this object to the specified object to determine their relative
         * order.
         *
         * @param   other   the object to compare to this instance.
         * @return  a negative integer if this instance is less than {@code another};
         *          a positive integer if this instance is greater than
         *          {@code another}; 0 if this instance has the same order as
         *          {@code another}.
         * @throws  ClassCastException  if {@code another} cannot be converted into something
         *                              comparable to {@code this} instance.
         */
        @Override
        public int compareTo(Identifier other) {
            if (this.uuid.compareTo(other.getUuid()) != 0)
                return this.uuid.compareTo(other.getUuid());
            if (this.getMajor().compareTo(other.getMajor()) != 0)
                return this.getMajor().compareTo(other.getMajor());
            return this.getMinor().compareTo(other.getMinor());
        }

        /**
         * overridden equals method
         *
         * @param   other   some other object
         * @return  whether or not this object is equal to another object
         */
        @Override
        public boolean equals(Object other) {
            try {
                return this.compareTo((Identifier) other) == 0;
            } catch(Exception ex) {
                return false;
            }
        }

        /**
         * overridden hashCode method
         *
         * @return  the {@link Object#hashCode()} for this
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.uuid.hashCode();
            hash = 79 * hash + this.major.hashCode();
            hash = 79 * hash + this.minor.hashCode();
            return hash;
        }

        /**
         * String representation for the id consisting of {@link #uuid}, {@link #major} &amp; {@link #minor}.
         *
         * @return  String representation of this
         */
        @Override
        public String toString() {
            return "UUID: " + this.uuid + ", major: " + this.major + ", minor: " + this.minor;
        }
    }



/*=======================================================
     __  __                                                    _
    |  \/  | ___  __ _ ___ _   _ _ __ ___ _ __ ___   ___ _ __ | |_
    | |\/| |/ _ \/ _` / __| | | | '__/ _ \ '_ ` _ \ / _ \ '_ \| __|
    | |  | |  __/ (_| \__ \ |_| | | |  __/ | | | | |  __/ | | | |_
    |_|  |_|\___|\__,_|___/\__,_|_|  \___|_| |_| |_|\___|_| |_|\__|

 =======================================================*/
    /**
     * Class to (de-)serialize Measurements for a {@link de.hsmainz.geoinform.hsmainzranging.BeaconLogObject}.
     *
     * @author  KekS (mailto:keks@keksfabrik.eu), 22.10.2014.
     */
    public static class Measurement implements Comparable<Measurement> {

        private long      timestamp;
        private int       rssi;
        private int       txPower;
        private double    calcDistance;


        /**
         * Default Constructor for a Measurement without external timestamp.
         *
         * @param   rssi            received signal strength indicator
         * @param   txPower         transmit power
         * @param   calcDistance    calculated distance
         */
        public Measurement(int rssi, int txPower, double calcDistance) {
            this(System.currentTimeMillis(), rssi, txPower, calcDistance);
        }


        /**
         * Default Constructor for a Measurement with external timestamp.
         *
         * @param   timestamp       timestamp of the Measurement
         * @param   rssi            received signal strength indicator
         * @param   txPower         transmit power
         * @param   calcDistance    calculated distance
         */
        public Measurement(long timestamp, int rssi, int txPower, double calcDistance) {
            this.timestamp = timestamp;
            this.rssi = rssi;
            this.txPower = txPower;
            this.calcDistance = calcDistance;
        }


        /**
         * Getter for {@link #rssi}
         *
         * @return  received signal strength indicator
         */
        public int getRssi() {
            return rssi;
        }

        /**
         * Getter for {@link #txPower}
         *
         * @return  transmit power
         */
        public int getTxPower() {
            return txPower;
        }

        /**
         * Getter for {@link #timestamp}
         *
         * @return  the timestamp
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Getter for {@link #getCalcDistance()}
         *
         * @return  the calculated proximity of a beacon
         */
        public double getCalcDistance() {
            return calcDistance;
        }

        /**
         * Converts the Radius Networks beacon proximity value passed in and returns an
         * appropriate human readable String.
         *
         * @return  human readable String representing the proximity of this measurement
         */
        public String getProximityString() {
            return getProximityString(calcDistance);
        }

        /**
         * Converts the Radius Networks beacon proximity value passed in and returns an
         * appropriate human readable String.
         * @param   proximity   double value expressing proximity in metres from Radius Networks beacon class
         * @return  human readable String representing that proximity
         */
        public static String getProximityString(double proximity) {
            String proximityString;
            if (proximity == -1.0) {
                // -1.0 is passed back by the SDK to indicate an unknown distance
                proximityString = "Unknown";
            } else if (proximity < 0.5) {
                proximityString = "Immediate";
            } else if (proximity < 2.0) {
                proximityString = "Near";
            } else {
                proximityString = "Far";
            }
            return proximityString;
        }

        /**
         * String representation for the id consisting of {@link #timestamp}, {@link #rssi},
         * {@link #txPower} &amp; {@link #calcDistance}
         *
         * @return  String representation of this
         */
        @Override
        public String toString() {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            return (df.format(new java.util.Date(timestamp)))
                    + ", rssi " + this.rssi + ", " + this.txPower + "dB, ~" + this.calcDistance + "m.";
        }

         /**
         * Compares this object to the specified object to determine their relative
         * order.
         *
         * @param   other   the object to compare to this instance.
         * @return  a negative integer if this instance is less than {@code another};
         *          a positive integer if this instance is greater than
         *          {@code another}; 0 if this instance has the same order as
         *          {@code another}.
         *
         * @throws ClassCastException if {@code another} cannot be converted into something
         *                            comparable to {@code this} instance.
         */
        @Override
        public int compareTo(Measurement other) {
            if (this.timestamp - other.getTimestamp() != 0)
                return new Long(this.timestamp - other.getTimestamp()).intValue();
            if (this.rssi - other.getRssi() != 0)
                return this.rssi - other.getRssi();
            if (this.txPower - other.getTxPower() != 0)
                return this.txPower - other.getTxPower();
            return this.calcDistance > other.getCalcDistance() ? 1 : this.calcDistance < other.getCalcDistance() ? -1 : 0;
        }
    }
}

package de.hsmainz.gi.beaconrangeevaluation;

import com.google.gson.Gson;
import de.hsmainz.gi.beaconrangeevaluation.model.BeaconLog;
import de.hsmainz.gi.beaconrangeevaluation.model.BeaconLogObject;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author KekS, Martin Saufaus
 */
public class Main {

    private static final Logger     L = Logger.getLogger(Main.class);
    private static final Gson       gson = new Gson();

    public Main() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JavaScript Object Files (*.json)", "json");
        fileChooser.setFileFilter(filter);

        // Get a DescriptiveStatistics instance (Apache commons math)
        ArrayList<DescriptiveStatistics> stats;
        List<BeaconLogObject> blist = null;
        BeaconLog bLog = null;

        // Workbook for output
        WritableWorkbook workbook = null;


        File[] selectedFiles = null;

        // Details from the mobile phone
        String manufacturer = "";
        String model = "";
        String androidVersion = "";

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {

            selectedFiles = fileChooser.getSelectedFiles();

            // Open a new excel workbook
            try {
                workbook = Workbook.createWorkbook(new File(selectedFiles[0].getAbsoluteFile().getParent(),"output.xls"));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Creating output.xls failed failed.");
            }

            // Create cell formats
            WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 12);
            WritableCellFormat textformat = new WritableCellFormat(arial12font);        // Text
            NumberFormat fivedps = new NumberFormat("#.#####");
            WritableCellFormat floatFormat = new WritableCellFormat(fivedps);           // Numbers
            DateFormat customDateFormat = new DateFormat ("dd MMM yyyy hh:mm:ss");
            WritableCellFormat dateFormat = new WritableCellFormat (customDateFormat);  // Dates


            // Summary sheet
            WritableSheet summarySheet = workbook.createSheet("Zusammenfassung", 0);
            int sRow = 0;
            int sCol = 0;

            try {
                summarySheet.addCell(new Label(sCol, sRow,    "UUID", textformat));
                summarySheet.addCell(new Label(sCol, sRow+1,  "Major", textformat));
                summarySheet.addCell(new Label(sCol, sRow+2,  "Minor", textformat));
                summarySheet.addCell(new Label(sCol, sRow+3,  "# der Messungen", textformat));
                summarySheet.addCell(new Label(sCol, sRow+4,  "Mittelwert", textformat));
                summarySheet.addCell(new Label(sCol, sRow+5,  "Stdabw.", textformat));
                summarySheet.addCell(new Label(sCol, sRow+6,  "Min", textformat));
                summarySheet.addCell(new Label(sCol, sRow+7,  "Max", textformat));

                sCol = 1;

            } catch (WriteException e) {
                e.printStackTrace();
                System.out.println("Initialisieren der Zusammenfassung schlug fehl!");
            }


            for (int k = 0; k < selectedFiles.length; k++) {

                blist = null;
                stats = new ArrayList<DescriptiveStatistics>();
                bLog = null;
                L.info("Selected file: " + selectedFiles[k].getAbsolutePath());

                try {
                    String json = readFile(selectedFiles[k].toString(), Charset.defaultCharset());
                    bLog = gson.fromJson(json, BeaconLog.class);

                    // Mobile phone details
                    manufacturer = bLog.getModel().getmManufacturer();
                    model = bLog.getModel().getmModel();
                    androidVersion = bLog.getModel().getmVersion();

/* ================= ADD YOUR CALCULATIONS HERE ================= */

                /* example: get just the beacons, print them */
                    blist = bLog.getLoggedBeacons();

                    printAverageDistances(blist);
                    List<BeaconLogObject.Measurement> tempObservation = null;

                    for (int i = 0; i < blist.size(); i++) {
                        tempObservation = blist.get(i).getMeasurements();
                        DescriptiveStatistics tempStats = new DescriptiveStatistics();

                        for (int j = 0; j < tempObservation.size(); j++) {
                            tempStats.addValue(tempObservation.get(j).getCalcDistance());
                            System.out.println(tempObservation.get(j).getCalcDistance());
                        }
                        stats.add(tempStats);

                        System.out.println("Read dataset " + (i + 1) + "| # of Elements: " + tempObservation.size());
                    }

/* ================= END OF YOUR CALCULATIONS ================= */

                } catch (IOException ex) {
                    L.warn("failed to read file", ex);
                }

                // Write observations + statistics to excel
                try {
                    // Add sheet to workbook
                    WritableSheet sheet = workbook.createSheet(selectedFiles[k].getName().substring(6), k+1);

                    // Columns and rows for
                    int row = 0;

                    // Header
                    sheet.addCell(new Label(6, row, "Hersteller:", textformat));
                    sheet.addCell(new Label(7, row, manufacturer + " / " + model, textformat));

                    sheet.addCell(new Label(6, row + 1, "Android Ver.:", textformat));
                    sheet.addCell(new Label(7, row + 1, androidVersion, textformat));

                    //row = 4;

                    // Observations + statistics
                    for (int i = 0; i < stats.size(); i++) {


                        for (int j = 0; j < blist.get(i).getMeasurements().size(); j++) {

//                            sheet.addCell(new DateTime(0, j + row + 1, blist.get(i).getMeasurements().get(j).getTimestamp(), dateFormat));    // Timestamp
                            sheet.addCell(new Number(1, j + row + 1, blist.get(i).getMeasurements().get(j).getCalcDistance(), floatFormat));    // Distance
                            sheet.addCell(new Number(2, j + row + 1, blist.get(i).getMeasurements().get(j).getRssi(), floatFormat));            // RSSI
                            sheet.addCell(new Number(3, j + row + 1, blist.get(i).getMeasurements().get(j).getTxPower(), floatFormat));         // txPower

                            if (j == 0) {

                                // Header
                                sheet.addCell(new Label(0, row, "Zeit", textformat));
                                sheet.addCell(new Label(1,row, "Distanz [m]", textformat));
                                sheet.addCell(new Label(2,row, "RSSI [dBm]", textformat));
                                sheet.addCell(new Label(3,row, "txPower [dBm]", textformat));

                                // Details for beacon
                                sheet.addCell(new Label(6, j + row + 2, "Beacon Major/Minor:", textformat));
                                sheet.addCell(new Label(7, j + row + 2, bLog.getLoggedBeacons().get(i).getID(), textformat));

                                // Add statistics to summarySheet

                                summarySheet.addCell(new Label(sCol, sRow, blist.get(i).getIdentifier().getUuid(), textformat));                // UUID
                                summarySheet.addCell(new Label(sCol, sRow+1, blist.get(i).getIdentifier().getMajor(), textformat));             // Major
                                summarySheet.addCell(new Label(sCol, sRow+2,  blist.get(i).getIdentifier().getMinor(), textformat));            // Minor
                                summarySheet.addCell(new Number(sCol, sRow+3, stats.get(i).getN()));                                            // # of measurements
                                summarySheet.addCell(new Number(sCol, sRow+4, stats.get(i).getMean(), floatFormat));                            // Mean value
                                summarySheet.addCell(new Number(sCol, sRow+5, stats.get(i).getStandardDeviation(), floatFormat));               // Standard deviation
                                summarySheet.addCell(new Number(sCol, sRow+6, stats.get(i).getMin(), floatFormat));                             // Minimum value
                                summarySheet.addCell(new Number(sCol, sRow+7, stats.get(i).getMax(), floatFormat));                             // Maximum value

                                sCol = sCol +1;
                            }


                        }

                        // Padding between multiple beacons in one .json
                        row += blist.get(i).getMeasurements().size() + 2;
                    }
                } catch (RowsExceededException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }


            }

            // Compiling and saving the workbook
            try {
                workbook.write();
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
                System.out.println("Writing the woorkbook failed. Check your rights!");
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    /**
     * print all beacon Objects in the file
     *
     * @param blist the list to print
     */
    public void printAverageDistances(List<BeaconLogObject> blist) {
        blist.stream()
            .forEach(
                (x) -> L.info(
                    x.toString() + "\t"
                    + x.getMeasurements()
                    .stream()
                    .mapToDouble(
                        BeaconLogObject.Measurement::getCalcDistance
                    )
                    .average()
                    .getAsDouble()
                    + "")
            );
    }

    /**
     * utility method to return a {@link java.lang.String} from a
     * {@link java.io.File} containing the entire content of the file specified
     * by {@code path}
     *
     * @param path the path to the file to read
     * @param encoding the charset to use when reading the file
     * @return the entire content of the file specified by {@code path}
     * @throws IOException if the file could not be read
     */
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void printFile(String fName){

    }
    /**
     * starter
     *
     * @param args starter params, IGNORED
     */
    public static void main(String... args) {
        /* setup logger */
        Properties props = new Properties();
        props.put("log4j.rootLogger", "ALL, stdout");
        props.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        props.put("log4j.appender.stdout.Target", "System.out");
        props.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.stdout.layout.ConversionPattern", "%d{HH:mm:ss.SSS} %-5p %c{1}:%L - %m%n");
        PropertyConfigurator.configure(props);
        
        Main m = new Main();
    }
}

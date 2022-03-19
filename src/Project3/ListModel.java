package Project3;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

import static Project3.ScreenDisplay.CurrentParkStatus;


/*****************************************************************
 * The purpose this class is to simulate a ListModel for
 * a CampReservationSystem
 *
 * @author Ricardo Herrera-Santos & Larry Goto
 * @version Winter 2020
 *****************************************************************/
public class ListModel extends AbstractTableModel {

    /** The list of campsites */
    private LinkedList<CampSite> listCampSites;

    /** The sorted/filtered list of campsites */
    private LinkedList<CampSite> fileredListCampSites;

    /** The display shown */
    private ScreenDisplay display;

    /** The column names used for displaying CurrentPark, SortRVTent, SortTentRV */
    private String[] columnNamesCurrentPark = {"Guest Name", "Est. Cost",
            "Check in Date", "EST. Check out Date ", "Max Power", "Num of Tenters"};

    /** The column names used for displaying Checked out campsites */
    private String[] columnNamesforCheckouts = {"Guest Name", "Est. Cost",
            "Check in Date", "ACTUAL Check out Date ", " Real Cost"};

    /** The column names used for displaying Overdue campsites */
    private String[] columnNamesOverdue = {"Guest Name", "Est. Cost", "EST. Check Out Date",
            "Days Overdue"};

    /** The formatter for date formats */
    private DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    /** The calendar used to hold and overdue date */
    private GregorianCalendar dateOverdue;

    /** The default constructor for ListModel */
    public ListModel() {
        display = CurrentParkStatus;
        listCampSites = new LinkedList<CampSite>();
        UpdateScreen();
        createList();
    }

    /******************************************************************
     * Void method that sets screen to passed input parameter
     *
     * @param selected screen to be switched to in GUI
     ******************************************************************/
    public void setDisplay(ScreenDisplay selected) {
        display = selected;
        UpdateScreen();
    }

    /******************************************************************
     * Void method that updates the screen based on the current
     * display variable
     ******************************************************************/
    private void UpdateScreen() {
        switch (display) {
                /** Case for displaying the current campsites in the park
                * Uses lambda functions */
            case CurrentParkStatus:
                fileredListCampSites = (LinkedList<CampSite>) listCampSites.stream()
                        .filter(n -> n.actualCheckOut == null)
                        .collect(Collectors.toCollection(LinkedList::new));

                Collections.sort(fileredListCampSites, (n1, n2) -> n1.getGuestName().compareTo(n2.guestName));
                break;

                /** Case for displaying checked out campsites
                * Uses anonymous class to sort */
            case CheckOutGuest:
                fileredListCampSites = (LinkedList<CampSite>) listCampSites.stream()
                        .filter(n -> n.getActualCheckOut() != null)
                        .collect(Collectors.toCollection(LinkedList::new));

                Collections.sort(fileredListCampSites, new Comparator<CampSite>() {
                    @Override
                    public int compare(CampSite n1, CampSite n2) {
                        return n1.getGuestName().compareTo(n2.guestName);
                    }
                });
                break;

                /** Case for displaying campsites passed their estimated checkout date
                * Uses lambda functions */
            case OverdueGuest:
                String enteredDate = JOptionPane.showInputDialog ("Enter Date (e.g., MM/DD/YYYY)");
                SimpleDateFormat df = new SimpleDateFormat("mm/dd/yyyy");

                try{
                    Date tempDate = df.parse(enteredDate);
                    dateOverdue = new GregorianCalendar();
                    this.dateOverdue.setTime(tempDate);

                    fileredListCampSites = ((LinkedList<CampSite>) listCampSites.stream()
                            .filter(n -> n.getEstimatedCheckOut() != null)
                            .collect(Collectors.toCollection(LinkedList::new)));

                    Collections.sort(fileredListCampSites, (n1, n2) ->
                            n1.getEstimatedCheckOut().compareTo(n2.getEstimatedCheckOut()));

                } catch (ParseException e) {
                    throw new RuntimeException("Invalid Date Entered");
                }
                break;

                /** Case for displaying sorted list of RVs then Tents
                * Uses lambda functions */
            case RvTent:
                fileredListCampSites = (LinkedList<CampSite>) listCampSites.stream().
                        filter(n -> n != null)
                        .collect(Collectors.toCollection(LinkedList::new));

                Collections.sort(fileredListCampSites, (n1, n2) -> n1.getGuestName().compareTo(n2.getGuestName()));
                Collections.sort(fileredListCampSites, (n1, n2) -> n1.getClass().toString().
                        compareTo(n2.getClass().toString()));

                break;

                /** Case for displaying sorted list of Tents then RVs
                * Uses anonymous class for sorting */
            case TentRv:
                fileredListCampSites = (LinkedList<CampSite>) listCampSites.stream().
                        filter(n -> n != null)
                        .collect(Collectors.toCollection(LinkedList::new));

                Collections.sort(fileredListCampSites, new Comparator<CampSite>() {
                    @Override
                    public int compare(CampSite n1, CampSite n2) {
                        int c;
                        c = -1 * (n1.getClass().toString().compareTo(n2.getClass().toString()));
                        if (c == 0)
                            c = n1.getGuestName().compareTo(n2.getGuestName());
                        if (c == 0)
                            c = n1.getEstimatedCheckOut().compareTo(n2.getEstimatedCheckOut());
                        return c;
                    }
                });

                break;

            default:
                throw new RuntimeException("upDate is in undefined state: " + display);
        }
        fireTableStructureChanged();
    }

    /******************************************************************
     * Get method for the column names to be used
     ******************************************************************/
    @Override
    public String getColumnName(int col) {
        switch (display) {
            case CurrentParkStatus:
            case RvTent:
            case TentRv:
                return columnNamesCurrentPark[col];
            case CheckOutGuest:
                return columnNamesforCheckouts[col];
            case OverdueGuest:
                return columnNamesOverdue[col];
        }
        throw new RuntimeException("Undefined state for Col Names: " + display);
    }

    /******************************************************************
     * Get method for the column amounts to be used
     ******************************************************************/
    @Override
    public int getColumnCount() {
        switch (display) {
            case CurrentParkStatus:
            case RvTent:
            case TentRv:
                return columnNamesCurrentPark.length;
            case CheckOutGuest:
                return columnNamesforCheckouts.length;
            case OverdueGuest:
                return columnNamesOverdue.length;

        }
        throw new IllegalArgumentException();
    }

    /******************************************************************
     * Get method for the amount of rows to be used
     ******************************************************************/
    @Override
    public int getRowCount() {
        return fileredListCampSites.size();     // returns number of items in the LinkedList
    }

    /******************************************************************
     * Get method for each individual unit
     ******************************************************************/
    @Override
    public Object getValueAt(int row, int col) {
        switch (display) {
            case CurrentParkStatus:
            case RvTent:
            case TentRv:
                return currentParkScreen(row, col);
            case CheckOutGuest:
                return checkOutScreen(row, col);
            case OverdueGuest:
                return overdueScreen(row, col);
        }
        throw new IllegalArgumentException();
    }

    /******************************************************************
     * Method used to create the park screen based on current
     * campsites
     *
     * Each case is a separate column
     ******************************************************************/
    private Object currentParkScreen(int row, int col) {
        switch (col) {
            case 0:
                return (fileredListCampSites.get(row).guestName);

            case 1:
                return (fileredListCampSites.get(row).getCost(fileredListCampSites.
                        get(row).estimatedCheckOut));

            case 2:
                return (formatter.format(fileredListCampSites.get(row).checkIn.getTime()));

            case 3:
                if (fileredListCampSites.get(row).estimatedCheckOut == null)
                    return "-";

                return (formatter.format(fileredListCampSites.get(row).estimatedCheckOut.
                        getTime()));

            case 4:
            case 5:
                if (fileredListCampSites.get(row) instanceof RV)
                    if (col == 4)
                        return (((RV) fileredListCampSites.get(row)).getPower());
                    else
                        return "";

                else {
                    if (col == 5)
                        return (((TentOnly) fileredListCampSites.get(row)).
                                getNumberOfTenters());
                    else
                        return "";
                }
            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

    /******************************************************************
     * Method used to create the park screen based on checked
     * out campsites
     *
     * Each case is a separate column
     ******************************************************************/
    private Object checkOutScreen(int row, int col) {
        switch (col) {
            case 0:
                return (fileredListCampSites.get(row).guestName);

            case 1:
                return (fileredListCampSites.
                        get(row).getCost(fileredListCampSites.get(row).
                        estimatedCheckOut));
            case 2:
                return (formatter.format(fileredListCampSites.get(row).checkIn.
                        getTime()));

            case 3:
                return (formatter.format(fileredListCampSites.get(row).actualCheckOut.
                        getTime()));

            case 4:
                return (fileredListCampSites.
                        get(row).getCost(fileredListCampSites.get(row).
                        actualCheckOut
                ));

            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

    /******************************************************************
     * Method used to create the park screen based on overdue
     * campsites
     *
     * Each case is a separate column
     ******************************************************************/
    private Object overdueScreen(int row, int col) {
        switch (col) {
            case 0:
                return (fileredListCampSites.get(row).guestName);

            case 1:
                return (fileredListCampSites.get(row).getCost(fileredListCampSites.
                        get(row).estimatedCheckOut));

            case 2:
                if (fileredListCampSites.get(row).estimatedCheckOut == null)
                    return "-";

                return (formatter.format(fileredListCampSites.get(row).estimatedCheckOut.
                        getTime()));

            case 3:
                if (fileredListCampSites.get(row).estimatedCheckOut == null)
                    return "-";

                return (int) ChronoUnit.DAYS.between(fileredListCampSites.get(row).estimatedCheckOut.toInstant(),
                        dateOverdue.toInstant());

            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

    /******************************************************************
     * Method used to add campsites to our list of campsites
     ******************************************************************/
    public void add(CampSite a) {
        listCampSites.add(a);
        UpdateScreen();
    }

    /******************************************************************
     * Method used to get a specific campsite in our list
     ******************************************************************/
    public CampSite get(int i) {
        return fileredListCampSites.get(i);
    }

    /******************************************************************
     * Method used to update specific campsites
     ******************************************************************/
    public void upDate(int index, CampSite unit) {
        UpdateScreen();
    }

    /******************************************************************
     * Method used to save the information to a non-legible file
     ******************************************************************/
    public void saveDatabase(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(listCampSites);
            os.close();
        } catch (IOException ex) {
            throw new RuntimeException("Saving problem! " + display);

        }
    }

    /******************************************************************
     * Method used to save the information to a legible text file
     ******************************************************************/
    public void saveText(String filename) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        } catch (IOException e) {
            throw new RuntimeException("Saving problem! " + display);
        }
        for (CampSite n : listCampSites) {
            if (n instanceof RV) {
                out.println("RV");
                out.println(n.getGuestName());
                out.println(formatter.format(n.getCheckIn().getTime()));
                out.println(formatter.format(n.getEstimatedCheckOut().getTime()));
                if (n.getActualCheckOut() != null)
                    out.println(formatter.format(n.getActualCheckOut().getTime()));
                else
                    out.println("--");
                out.println(((RV) n).getPower());
            } else {
                out.println("Tent");
                out.println(n.getGuestName());
                out.println(formatter.format(n.getCheckIn().getTime()));
                out.println(formatter.format(n.getEstimatedCheckOut().getTime()));
                if (n.getActualCheckOut() != null)
                    out.println(formatter.format(n.getActualCheckOut().getTime()));
                else
                    out.println("--");
                out.println(((TentOnly) n).getNumberOfTenters());
            }
        }
        out.close();
    }

    /******************************************************************
     * Method used to load the information from a non-legible file
     ******************************************************************/
    public void loadDatabase(String filename) {
        listCampSites.clear();

        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream is = new ObjectInputStream(fis);

            listCampSites = (LinkedList<CampSite>) is.readObject();
            UpdateScreen();
            is.close();
        } catch (Exception ex) {
            throw new RuntimeException("Loading problem: " + display);

        }
    }

    /******************************************************************
     * Method used to load the information from a legible text file
     ******************************************************************/
    public void loadText(String filename){
        listCampSites.clear();

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String tempGuestName;
        GregorianCalendar tempCheckIn = new GregorianCalendar();
        GregorianCalendar tempEstCheckOut = new GregorianCalendar();
        GregorianCalendar tempActual;
        tempActual = null;
        Date d1;
        Date d2;
        Date d3 = null;
        int tempPower;
        int tempTenters;


        try {
            Scanner scan = new Scanner(new File(filename));
            while (scan.hasNext()){
                if (scan.next().equals("RV")){
                    tempGuestName = scan.next();
                    d1 = df.parse(scan.next());
                    d2 = df.parse(scan.next());
                    String s = scan.next();
                    if (!s.equals("--")){
                        d3 = df.parse(s);
                        tempActual = new GregorianCalendar();
                        tempActual.setTime(d3);
                    } else {
                        tempActual = null;
                    }
                    tempCheckIn.setTime(d1);
                    tempEstCheckOut.setTime(d2);
                    tempPower = scan.nextInt();

                    listCampSites.add(new RV(tempGuestName, tempCheckIn,
                            tempEstCheckOut, tempActual, tempPower));
                } else {
                    tempGuestName = scan.next();
                    d1 = df.parse(scan.next());
                    d2 = df.parse(scan.next());
                    String s = scan.next();
                    if (!s.equals("--")) {
                        d3 = df.parse(s);
                        tempActual = new GregorianCalendar();
                        tempActual.setTime(d3);
                    } else {
                        tempActual = null;
                    }
                    tempCheckIn.setTime(d1);
                    tempEstCheckOut.setTime(d2);
                    tempTenters = scan.nextInt();

                    listCampSites.add(new TentOnly(tempGuestName, tempCheckIn,
                            tempEstCheckOut, tempActual, tempTenters));
                }
            }
            UpdateScreen();
            scan.close();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (ParseException e){
            throw new RuntimeException("Error in the creation of list");
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid Date in Text");
        }

    }

    /******************************************************************
     * Method used to create a sample beginning list
     ******************************************************************/
    public void createList() {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        GregorianCalendar g1 = new GregorianCalendar();
        GregorianCalendar g2 = new GregorianCalendar();
        GregorianCalendar g3 = new GregorianCalendar();
        GregorianCalendar g4 = new GregorianCalendar();
        GregorianCalendar g5 = new GregorianCalendar();
        GregorianCalendar g6 = new GregorianCalendar();

        try {
            Date d1 = df.parse("1/20/2020");
            g1.setTime(d1);
            Date d2 = df.parse("12/22/2020");
            g2.setTime(d2);
            Date d3 = df.parse("12/20/2019");
            g3.setTime(d3);
            Date d4 = df.parse("3/25/2020");
            g4.setTime(d4);
            Date d5 = df.parse("1/20/2010");
            g5.setTime(d5);
            Date d6 = df.parse("3/29/2020");
            g6.setTime(d6);

            TentOnly tentOnly1 = new TentOnly("T1", g3, g2,null,4);
            TentOnly tentOnly11 = new TentOnly("T1", g3,g1, null, 8);
            TentOnly tentOnly111 = new TentOnly("T1", g5,g3, null, 8);
            TentOnly tentOnly2 = new TentOnly("T2", g5, g3,null, 5);
            TentOnly tentOnly3 = new TentOnly("T3", g3, g1, g1,7);

            RV RV1 = new RV("RV1",g4,g6,null, 1000);
            RV RV2 = new RV("RV2",g5,g3,null, 1000);
            RV RV22 = new RV("RV2", g3,g1,null, 2000);
            RV RV222 = new RV("RV2", g3,g6,null, 2000);
            RV RV3 = new RV("RV3",g5,g4,g3, 1000);

            add(tentOnly1);
            add(tentOnly2);
            add(tentOnly3);
            add(tentOnly11);
            add(tentOnly111);

            add(RV1);
            add(RV2);
            add(RV3);
            add(RV22);
            add(RV222);

        } catch (ParseException e) {
            throw new RuntimeException("Error in testing, creation of list");
        }
    }
}


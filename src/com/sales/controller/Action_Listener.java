package com.sales.controller;
/**
 *
 * @author Marwa Abo ElWafa
 */
import com.sales.model.InvoHeader;
import com.sales.model.InvoTable_Model;
import com.sales.model.LineClass;
import com.sales.model.LineTableClass;
import com.sales.view.InvoDiaHeader;
import com.sales.view.InvoiceFrame;
import com.sales.view.InvoLineDia;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Action_Listener implements ActionListener, ListSelectionListener {

    private InvoiceFrame frame;
    private InvoDiaHeader invoiceDialog;
    private InvoLineDia lineDialog;

    public Action_Listener(InvoiceFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("Action: " + actionCommand);
        
        switch (actionCommand) {
            case "Load File":
                loadFile();
                break;
            case "Create New Invoice":
                create_New_Invoice();
                break;
            case "Delete Invoice":
                delete_Invoice();
                break;
            case "Create New Item":
                create_NewItem();
                break;
            case "Delete Item":
                deleteItem();
                break;
            case "createInvoiceCancel":
                createInvoiceCancel();
                break;
            case "createInvoiceAdd":
                createInvoiceOK();
                break;
            case "createLineAdd":
                createLineOK();
                break;
            case "createLineCancel":
                create_Line_Cancel();
                break;
            case "Save File":
                saveFile();
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = frame.get_Invo_Table().getSelectedRow();
        if (selectedIndex != -1) {
            System.out.println("You have selected row: " + selectedIndex);
            InvoHeader currentInvoice = frame.get_Invo().get(selectedIndex);
            frame.get_Invo_Numb_Label().setText("" + currentInvoice.getIdNumber());
            frame.get_Invoice_Date_Labell().setText("" + currentInvoice.getInvoiceDate());
            frame.get_Customer_Name_Label().setText(currentInvoice.getCustomerName());
            frame.get_Invo_Total_Label().setText("" + currentInvoice.getInvoiceTotal());
            LineTableClass linesTableModel = new LineTableClass(currentInvoice.getLines());
            frame.getLine_Tablee().setModel(linesTableModel);
            linesTableModel.fireTableDataChanged();
        }
    }

    private void loadFile() {
        JFileChooser fc = new JFileChooser();

        try {
            JOptionPane.showMessageDialog(frame, "Select Invoice Header File",
                    "Information Message", JOptionPane.INFORMATION_MESSAGE);
            int result = fc.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                Path headerPath = Paths.get(headerFile.getAbsolutePath());
                List<String> headerLines = Files.readAllLines(headerPath);
                System.out.println("Invoices have been read");
                // 1,22-11-2020,Ali
                // 2,13-10-2021,Saleh
                // 3,09-01-2019,Ibrahim
                ArrayList<InvoHeader> invoicesArray = new ArrayList<>();
                for (String headerLine : headerLines) {
                    try {
                        String[] headerParts = headerLine.split(",");
                        int invoiceNum = Integer.parseInt(headerParts[0]);
                        String invoiceDate = headerParts[1];
                        String customerName = headerParts[2];

                        InvoHeader invoice = new InvoHeader(invoiceNum, invoiceDate, customerName);
                        invoicesArray.add(invoice);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                        
                    }
                }
                System.out.println("Check point");
                JOptionPane.showMessageDialog(frame, "Select Invoice Line File",
                        "Information Message", JOptionPane.INFORMATION_MESSAGE);
                result = fc.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    Path linePath = Paths.get(lineFile.getAbsolutePath());
                    List<String> lineLines = Files.readAllLines(linePath);
                    System.out.println("Lines have been read");
                    for (String lineLine : lineLines) {
                        try {
                            String lineParts[] = lineLine.split(",");
                            int invoiceNum = Integer.parseInt(lineParts[0]);
                            String itemName = lineParts[1];
                            double itemPrice = Double.parseDouble(lineParts[2]);
                            int count = Integer.parseInt(lineParts[3]);
                            InvoHeader inv = null;
                            for (InvoHeader invoice : invoicesArray) {
                                if (invoice.getIdNumber() == invoiceNum) {
                                    inv = invoice;
                                    break;
                                }
                            }

                            LineClass line = new LineClass(itemName, itemPrice, count, inv);
                            inv.getLines().add(line);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                    
                        }
                    }
                    System.out.println("Check point");
                }
                frame.set_Invo(invoicesArray);
                InvoTable_Model invoicesTableModel = new InvoTable_Model(invoicesArray);
                frame.set_Invo_Table_Model(invoicesTableModel);
                frame.get_Invo_Table().setModel(invoicesTableModel);
                frame.get_Invo_Table_Model().fireTableDataChanged();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Cannot read file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile() {
        ArrayList<InvoHeader> invoices = frame.get_Invo();
        String headers = "";
        String lines = "";
        for (InvoHeader invoice : invoices) {
            String invCSV = invoice.getAsCSV();
            headers += invCSV;
            headers += "\n";

            for (LineClass line : invoice.getLines()) {
                String lineCSV = line.getAsCSV();
                lines += lineCSV;
                lines += "\n";
            }
        }
        System.out.println("Check point");
        
        try {
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                FileWriter hfw = new FileWriter(headerFile);
                hfw.write(headers);
                hfw.flush();
                hfw.close();
                result = fc.showSaveDialog(frame);
                JOptionPane.showMessageDialog(frame, "File saved successfully",
           "Information Message", JOptionPane.INFORMATION_MESSAGE);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    FileWriter lfw = new FileWriter(lineFile);
                    lfw.write(lines);
                    lfw.flush();
                    lfw.close();
                }
            }
        } catch (Exception ex) {
            

        }
    }

    private void create_New_Invoice() {
        invoiceDialog = new InvoDiaHeader(frame);
        invoiceDialog.setVisible(true);
    }

    private void delete_Invoice() {
        int selectedRow = frame.get_Invo_Table().getSelectedRow();
        if (selectedRow != -1) {
            frame.get_Invo().remove(selectedRow);
            frame.get_Invo_Table_Model().fireTableDataChanged();
            
        }
    }

    private void create_NewItem() {
        lineDialog = new InvoLineDia(frame);
        lineDialog.setVisible(true);
    }

    private void deleteItem() {
        int selectedRow = frame.getLine_Tablee().getSelectedRow();

        if (selectedRow != -1) {
            LineTableClass linesTableModel = (LineTableClass) frame.getLine_Tablee().getModel();
            linesTableModel.getLines().remove(selectedRow);
            linesTableModel.fireTableDataChanged();
            frame.get_Invo_Table_Model().fireTableDataChanged();
        }
    }

    private void createInvoiceCancel() {
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
    }

    private void createInvoiceOK() {
        String date = invoiceDialog.getInvDateField().getText();
        String customer = invoiceDialog.getCustNameField().getText();
        int num = frame.getNextInvo_Num();
        try {
            String[] dateParts = date.split("-");  // 
            if (dateParts.length < 3) {
                JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                if (day > 31 || month > 12) {
                    JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    InvoHeader invoice = new InvoHeader(num, date, customer);
                    frame.get_Invo().add(invoice);
                    frame.get_Invo_Table_Model().fireTableDataChanged();
                    invoiceDialog.setVisible(false);
                    invoiceDialog.dispose();
                    invoiceDialog = null;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void createLineOK() {
        String item = lineDialog.getItemNameField().getText();
        String countStr = lineDialog.getItemCountField().getText();
        String priceStr = lineDialog.getItemPriceField().getText();
        int count = Integer.parseInt(countStr);
        double price = Double.parseDouble(priceStr);
        int selectedInvoice = frame.get_Invo_Table().getSelectedRow();
        if (selectedInvoice != -1) {
            InvoHeader invoice = frame.get_Invo().get(selectedInvoice);
            LineClass line = new LineClass(item, price, count, invoice);
            invoice.getLines().add(line);
            LineTableClass linesTableModel = (LineTableClass) frame.getLine_Tablee().getModel();
            //linesTableModel.getLines().add(line);
            linesTableModel.fireTableDataChanged();
            frame.get_Invo_Table_Model().fireTableDataChanged();
        }
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

    private void create_Line_Cancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

}

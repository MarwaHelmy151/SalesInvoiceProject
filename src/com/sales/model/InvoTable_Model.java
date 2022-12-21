package com.sales.model;

/**
 *
 * @author Marwa Abo ElWafa
 */

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class InvoTable_Model extends AbstractTableModel {
    private ArrayList<InvoHeader> invoices;
    private String[] columns = {"No.", "Date", "Customer", "Total"};
    
    public InvoTable_Model(ArrayList<InvoHeader> invoices) {
        this.invoices = invoices;
    }
    
    @Override
    public int getRowCount() {
        return invoices.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InvoHeader invoice = invoices.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return invoice.getIdNumber();
            case 1: return invoice.getInvoiceDate();
            case 2: return invoice.getCustomerName();
            case 3: return invoice.getInvoiceTotal();
            default : return "";
        }
    }
}
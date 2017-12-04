/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphreuse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

/**
 * @author User
 */
class SpinnerEditor extends DefaultCellEditor {

    JSpinner spinner;
    JSpinner.DefaultEditor editor;
    JTextField textField;
    boolean valueSet;

    public SpinnerEditor() {
        super(new JTextField());
        spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        editor = ((JSpinner.DefaultEditor) spinner.getEditor());
        textField = editor.getTextField();
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (valueSet) {
                            textField.setCaretPosition(1);
                        }
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent fe) {
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                stopCellEditing();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        if (!valueSet) {
            spinner.setValue(value);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textField.requestFocus();
            }
        });
        return spinner;
    }

    @Override
    public boolean isCellEditable(EventObject eo) {
        if (eo instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) eo;
            textField.setText(String.valueOf(ke.getKeyChar()));
            valueSet = true;
        } else {
            valueSet = false;
        }
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public boolean stopCellEditing() {
        try {
            editor.commitEdit();
            spinner.commitEdit();
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid value, discarding.");
        }
        return super.stopCellEditing();
    }
}

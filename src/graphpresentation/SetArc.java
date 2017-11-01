/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SetTie.java
 *
 * Created on 11.12.2011, 18:44:06
 */
package graphpresentation;

/**
 *
 * @author Оля
 */
public class SetArc extends javax.swing.JFrame {

    /**
     * Creates new form SetTie
     */
    public SetArc(PetriNetsPanel panel) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.panel = panel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setTiePanel = new javax.swing.JPanel();
        quantityLabel = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        quantityTextField = new javax.swing.JTextField();
        isInfRadioButton = new javax.swing.JRadioButton();
        jTextField2 = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Параметри зв'язку");

        setTiePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Основні параметри"));

        quantityLabel.setText("Кількість зв'язків");

        jRadioButton1.setText("Використовувати формулу");
        jRadioButton1.setEnabled(false);

        isInfRadioButton.setText("Інформаційний зв'язок");

        jTextField2.setEnabled(false);

        javax.swing.GroupLayout setTiePanelLayout = new javax.swing.GroupLayout(setTiePanel);
        setTiePanel.setLayout(setTiePanelLayout);
        setTiePanelLayout.setHorizontalGroup(
            setTiePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setTiePanelLayout.createSequentialGroup()
                .addGroup(setTiePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(setTiePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(setTiePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(setTiePanelLayout.createSequentialGroup()
                                .addComponent(quantityLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(quantityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jRadioButton1)
                            .addComponent(isInfRadioButton)))
                    .addGroup(setTiePanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        setTiePanelLayout.setVerticalGroup(
            setTiePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setTiePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(setTiePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(quantityLabel)
                    .addComponent(quantityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(isInfRadioButton)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        okButton.setText("Ок");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Відмінити");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(setTiePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(setTiePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        choosenTie = null;        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
       try{
        setQuantity();
        setIsInf();
        choosenTie = null;
        this.setVisible(false);
        panel.repaint();
       }
         catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    private void setQuantity() {
       
            choosenTie.setQuantity(Integer.valueOf(quantityTextField.getText()));
       

    }

    private void getQuantity() {
        quantityTextField.setText(Integer.toString(choosenTie.getQuantity()));
    }

    private void setIsInf() {
        choosenTie.setInf(isInfRadioButton.isSelected());

    }

    private void getIsInf() {
        isInfRadioButton.setSelected(choosenTie.getIsInf());
    }

    private void setChoosenTie(GraphArc t) {
        choosenTie = t;
    }

    public void setInfo(GraphArc t) {
        setChoosenTie(t);
        getQuantity();
        getIsInf();
    }
    private PetriNetsPanel panel;
    private GraphArc choosenTie;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton isInfRadioButton;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JTextField quantityTextField;
    private javax.swing.JPanel setTiePanel;
    // End of variables declaration//GEN-END:variables
}

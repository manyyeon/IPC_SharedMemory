package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// setting dialog 클래스
class SettingDialog extends JDialog {
    JPanel dialogPanel = new JPanel();
    JTextField inputBoundedBufferSize = new JTextField(5);
    JTextField inputEquationNumber = new JTextField(5);
    JButton okButton = new JButton("확인");

    public SettingDialog(JFrame frame, String title){
        super(frame, title, true); // modal dialog 만들기
        this.setLocation(750,500);
        setLayout(new BorderLayout());

        dialogPanel.add(new JLabel("Bounded Buffer 크기 : "));
        dialogPanel.add(inputBoundedBufferSize);
        dialogPanel.add(new JLabel("Equation 발생 횟수 : "));
        dialogPanel.add(inputEquationNumber);
        add(dialogPanel, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);

        setSize(250,150);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
    }

    public String getInputBoundedBufferSize() {
        if(inputBoundedBufferSize.getText().length() == 0){
            return null;
        } else{
            String tmpText = inputBoundedBufferSize.getText();
            inputBoundedBufferSize.setText("");
            return tmpText;
        }
    }
    public String getInputEquationNumber() {
        if(inputEquationNumber.getText().length() == 0){
            return null;
        } else{
            String tmpText = inputEquationNumber.getText();
            inputEquationNumber.setText("");
            return tmpText;
        }
    }
}
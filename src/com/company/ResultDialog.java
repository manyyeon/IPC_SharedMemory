package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// ResultDialog 클래스
class ResultDialog extends JDialog {
    JPanel dialogPanel = new JPanel();
    JButton okButton = new JButton("확인");

    public ResultDialog(JFrame frame, String title, int bufferSize, int equationNumber){
        super(frame, title, true); // modal dialog 만들기
        this.setLocation(750,500);
        setLayout(new BorderLayout());

        dialogPanel.add(new JLabel("  메모리 크기 = " + bufferSize));
        dialogPanel.add(new JLabel("총 " + equationNumber + "개의 식을 계산했습니다."));
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
}
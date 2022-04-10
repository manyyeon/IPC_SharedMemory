package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// ResultDialog 클래스
// 계산이 다 끝나고 나서 결과를 띄워주는 dialog
class ResultDialog extends JDialog {
    JPanel dialogPanel = new JPanel(); // 패널
    JButton okButton = new JButton("확인"); // 확인 버튼

    public ResultDialog(JFrame frame, String title, int bufferSize, int equationNumber){
        super(frame, title, true); // modal dialog 만들기
        this.setLocation(750,500); // 위치 지정
        setLayout(new BorderLayout()); // 레이아웃 설정

        // 패널에 출력해줄 것들 붙이기
        dialogPanel.add(new JLabel("  메모리 크기 = " + bufferSize));
        dialogPanel.add(new JLabel("총 " + equationNumber + "개의 식을 계산했습니다."));
        add(dialogPanel, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);

        setSize(250,150); // 사이즈 설정

        // 확인 버튼 이벤트 리스너
        okButton.addActionListener(new ActionListener() {
            @Override
            // 클릭하면
            public void actionPerformed(ActionEvent e) {
                // dialog 안보이게 숨기기
                setVisible(false);
            }
        });
    }
}
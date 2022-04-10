package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// setting dialog 클래스
class SettingDialog extends JDialog {
    // 패널 생성
    JPanel dialogPanel = new JPanel();
    // bufferSize와 equationNumber를 받을 텍스트필드 생성
    JTextField inputBoundedBufferSize = new JTextField(5);
    JTextField inputEquationNumber = new JTextField(5);
    // 확인 버튼 생성
    JButton okButton = new JButton("확인");

    public SettingDialog(JFrame frame, String title){
        // modal dialog 만들기
        // modal dialog가 창 띄워져 있을 때 다른 동작 못하는 것
        super(frame, title, true);
        // dialog 위치 설정
        this.setLocation(750,500);
        // 레이아웃 설정
        setLayout(new BorderLayout());

        // 패널에 붙이기
        dialogPanel.add(new JLabel("Bounded Buffer 크기 : "));
        dialogPanel.add(inputBoundedBufferSize);
        dialogPanel.add(new JLabel("Equation 발생 횟수 : "));
        dialogPanel.add(inputEquationNumber);
        // 패널과 확인 버튼을 컨텐트팬에 붙이기
        add(dialogPanel, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);

        // 사이즈 설정
        setSize(250,150);

        // 확인 버튼에 연결된 이벤트 리스너
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // dialog 안보이게 하기
                setVisible(false);
            }
        });
    }

    // bufferSize 반환해주는 함수
    public String getInputBoundedBufferSize() {
        if(inputBoundedBufferSize.getText().length() == 0){
            return null;
        } else{
            // 텍스트필드에서 getText()로 입력 받은 문자열 가져오기
            String tmpText = inputBoundedBufferSize.getText();
            // 텍스트필드 초기화
            inputBoundedBufferSize.setText("");
            // 입력 받은 문자열 리턴
            return tmpText;
        }
    }
    // equationNumber 반환해주는 함수
    public String getInputEquationNumber() {
        if(inputEquationNumber.getText().length() == 0){
            return null;
        } else{
            // 텍스트필드에서 getText()로 입력 받은 문자열 가져오기
            String tmpText = inputEquationNumber.getText();
            // 텍스트필드 초기화
            inputEquationNumber.setText("");
            // 입력 받은 문자열 리턴
            return tmpText;
        }
    }
}
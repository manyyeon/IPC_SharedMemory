package com.company;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MyFrame extends JFrame {
    SettingDialog settingDialog;
    JPanel menuPanel = new JPanel();
    JPanel titlePanel = new JPanel();
    JButton [] menuButton = new JButton[3];
    String [] menuText = {"START", "INITIALIZATION", "SETTING"};
    JLabel [] titleLabel = new JLabel[3];
    String [] titleText = {"Producer", "Bounded Buffer", "Consumer"};

    public MyFrame(){
        setTitle("Shared Memory IPC 통신"); // 윈도우 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우를 끄면 프로그램 종료

        Container contentPane = getContentPane(); // 컨텐트팬 가져오기
        this.setLocation(400,300); // 윈도우 위치 설정

        // menuPanel, titlePanel 레이아웃 설정
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS)); // BoxLayout 수평으로
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS)); // BoxLayout 수평으로

        // menu 컴포넌트들 생성
        for(int i=0; i< menuButton.length; i++){
            menuButton[i] = new JButton(menuText[i]);
        }

        // menuPanel에 부착
        menuPanel.add(menuButton[0]);
        menuPanel.add(Box.createHorizontalGlue()); // 빈 컴포넌트 삽입
        menuPanel.add(menuButton[1]);
        menuPanel.add(Box.createHorizontalGlue()); // 빈 컴포넌트 삽입
        menuPanel.add(menuButton[2]);

        // title 컴포넌트들 생성 및 글씨체 설정
        for(int i=0; i<titleLabel.length; i++){
            titleLabel[i] = new JLabel(titleText[i]);
            titleLabel[i].setFont(new Font("맑은고딕", Font.BOLD, 15));
        }

        // titlePanel에 부착
        titlePanel.add(titleLabel[0]);
        titlePanel.add(Box.createHorizontalGlue()); // 빈 컴포넌트 삽입
        titlePanel.add(titleLabel[1]);
        titlePanel.add(Box.createHorizontalGlue()); // 빈 컴포넌트 삽입
        titlePanel.add(titleLabel[2]);

        // 컨텐트팬에 패널 부착
        contentPane.add(menuPanel, BorderLayout.SOUTH); // 남쪽에 배치
        contentPane.add(titlePanel, BorderLayout.NORTH); // 북쪽에 배치

        // settingDialog 생성
        settingDialog = new SettingDialog(this, "Buffer Size, Equation Number 설정");

        // 이벤트 리스너 달기
        menuButton[0].addActionListener(new StartActionListener());
        menuButton[1].addActionListener(new InitializationActionListener());
        menuButton[2].addActionListener(new SettingActionListener());

        // 컨텐트팬 사이즈 설정
        setSize(1000, 500);
        setVisible(true);
    }

    class StartActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.out.println("start");
        }
    }

    class InitializationActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.out.println("initialization");
        }
    }

    class SettingActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.out.println("setting");
            settingDialog.setVisible(true);
        }
    }

    public static void main(String[] args){
        MyFrame frame = new MyFrame();
    }
}

// setting dialog 클래스
class SettingDialog extends JDialog {
    JPanel dialogPanel = new JPanel();
    JTextField inputBoundedBufferSize = new JTextField(5);
    JTextField inputEquationNumber = new JTextField(5);
    JButton okButton = new JButton("확인");

    public SettingDialog(JFrame frame, String title){
        super(frame, title, true);
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
                System.out.println("확인");
                setVisible(false);
            }
        });
    }


}
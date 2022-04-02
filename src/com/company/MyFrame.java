package com.company;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class MyFrame extends JFrame {
    public MyFrame(){
        setTitle("Shared Memory IPC 통신"); // 윈도우 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우를 끄면 프로그램 종료

        Container contentPane = getContentPane(); // 컨텐트팬 가져오기
        this.setLocation(400,300); // 윈도우 위치 설정

        // menuPanel, titlePanel 생성
        JPanel menuPanel = new JPanel();
        JPanel titlePanel = new JPanel();

        // menuPanel, titlePanel 레이아웃 설정
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS)); // BoxLayout 수평으로
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS)); // BoxLayout 수평으로

        // menu 컴포넌트들 생성
        JButton menuButton1 = new JButton("START");
        JButton menuButton2 = new JButton("INITIALIZATION");
        JButton menuButton3 = new JButton("SETTING");

        // menuPanel에 부착
        menuPanel.add(menuButton1);
        menuPanel.add(Box.createHorizontalGlue()); // 빈 컴포넌트 삽입
        menuPanel.add(menuButton2);
        menuPanel.add(Box.createHorizontalGlue()); // 빈 컴포넌트 삽입
        menuPanel.add(menuButton3);

        // title 컴포넌트들 생성
        JLabel titleLabel1 = new JLabel("Producer");
        JLabel titleLabel2 = new JLabel("Bounded Buffer");
        JLabel titleLabel3 = new JLabel("Consumer");

        // title 컴포넌트들 글씨체 설정
        titleLabel1.setFont(new Font("맑은고딕", Font.BOLD, 15));
        titleLabel2.setFont(new Font("맑은고딕", Font.BOLD, 15));
        titleLabel3.setFont(new Font("맑은고딕", Font.BOLD, 15));

        // titlePanel에 부착
        titlePanel.add(titleLabel1);
        titlePanel.add(Box.createHorizontalGlue()); // 빈 컴포넌트 삽입
        titlePanel.add(titleLabel2);
        titlePanel.add(Box.createHorizontalGlue()); // 빈 컴포넌트 삽입
        titlePanel.add(titleLabel3);

        // 컨텐트팬에 패널 부착
        contentPane.add(menuPanel, BorderLayout.SOUTH); // 남쪽에 배치
        contentPane.add(titlePanel, BorderLayout.NORTH); // 북쪽에 배치

        // 이벤트 리스너 달기
        menuButton1.addMouseListener(new StartMouseListener());

        // 컨텐트팬 사이즈 설정
        setSize(1000, 500);
        setVisible(true);
    }

    class StartMouseListener extends MouseAdapter{
        public void mousePressed(MouseEvent e){
            System.out.println("start");
        }
    }

    class InitializationMouseListener extends MouseAdapter{
        public void mousePressed(MouseEvent e){
            System.out.println("initialization");
        }
    }

    class SettingMouseListener extends MouseAdapter{
        public void mousePressed(MouseEvent e){
            System.out.println("setting");
        }
    }

    public static void main(String[] args){
        MyFrame frame = new MyFrame();
    }
}
package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyFrame extends JFrame {
    public static MyFrame myFrame;

    Container contentPane;
    SettingDialog settingDialog; // 설정창
    SharedMemory sharedMemory; // 공유 메모리
    ProducerThread producerThread; // 생산자 스레드
    ConsumerThread consumerThread; // 소비자 스레드

    JPanel menuPanel = new JPanel();
    JPanel titlePanel = new JPanel();
    JPanel producePanel = new JPanel();
    JPanel bufferPanel = new JPanel();
    JPanel consumePanel = new JPanel();

    JButton [] menuButton = new JButton[3];
    String [] menuText = {"START", "INITIALIZATION", "SETTING"};
    JLabel [] titleLabel = new JLabel[3];
    String [] titleText = {"Producer", "Bounded Buffer", "Consumer"};

    int bufferSize = 3; // 버퍼 크기
    int equationNumber = 10; // 사칙연산 개수

    JLabel [] bufferBox; // 버퍼 공간
    JLabel [] produceBox; // 식 생산 공간
    JLabel [] consumeBox; // 식 계산해서 답 보여주는 공간

    public MyFrame(){
        setTitle("Shared Memory IPC 통신"); // 윈도우 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우를 끄면 프로그램 종료

        contentPane = getContentPane(); // 컨텐트팬 가져오기
        this.setLocation(400,300); // 윈도우 위치 설정

        // contentPane 레이아웃 설정
        contentPane.setLayout(new BorderLayout(5,5));

        // panel 레이아웃 설정
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS)); // BoxLayout 수평으로
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS)); // BoxLayout 수평으로
        producePanel.setLayout(new BoxLayout(producePanel, BoxLayout.Y_AXIS));
        bufferPanel.setLayout(new BoxLayout(bufferPanel, BoxLayout.Y_AXIS));
        consumePanel.setLayout(new BoxLayout(consumePanel, BoxLayout.Y_AXIS));

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
        contentPane.add(new JScrollPane(producePanel), BorderLayout.WEST); // 서쪽에 배치, 스크롤팬에 삽입
        contentPane.add(new JScrollPane(bufferPanel), BorderLayout.CENTER); // 중앙에 배치, 스크롤팬에 삽입
        contentPane.add(new JScrollPane(consumePanel), BorderLayout.EAST); // 동쪽에 배치, 스크롤팬에 삽입

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

    // 이벤트 리스너
    // start 리스너
    class StartActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            System.out.println("start");
            producerThread.start();
        }
    }
    // 초기화 리스너
    class InitializationActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.out.println("initialization");
        }
    }
    // setting 리스너
    class SettingActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.out.println("setting");
            settingDialog.setVisible(true);

            bufferSize = Integer.parseInt(settingDialog.getInputBoundedBufferSize());
            equationNumber = Integer.parseInt(settingDialog.getInputEquationNumber());

            // buffer size만큼 공간 만들기
            bufferBox = new JLabel[bufferSize];
            for(int i=0; i<bufferSize; i++){
                bufferBox[i] = new JLabel("(" + (i+1) + ") " + "buffer");
                bufferBox[i].setPreferredSize(new Dimension(300, 200));
                bufferPanel.add(bufferBox[i]);
            }
            // equation 개수만큼 공간 만들기
            produceBox = new JLabel[equationNumber];
            consumeBox = new JLabel[equationNumber];
            for(int i=0; i<equationNumber; i++){
                produceBox[i] = new JLabel("(" + (i+1) + ") " + "produce");
                consumeBox[i] = new JLabel("(" + (i+1) + ") " + "consume");
                produceBox[i].setPreferredSize(new Dimension(300, 200));
                consumeBox[i].setPreferredSize(new Dimension(300, 200));
                producePanel.add(produceBox[i]);
                consumePanel.add(consumeBox[i]);
            }

            // 공유메모리, 생산자, 소비자 스레드 생성
            sharedMemory = new SharedMemory(myFrame, equationNumber, bufferSize);
            producerThread = new ProducerThread(myFrame, sharedMemory, produceBox);
            consumerThread = new ConsumerThread(myFrame, sharedMemory);

            contentPane.revalidate(); // 자식 컴포넌트 다시 배치
        }
    }

    public static void main(String[] args){
        myFrame = new MyFrame();
    }
}
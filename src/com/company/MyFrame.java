package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyFrame extends JFrame {
    // myFrame을 static 변수로 선언
    public static MyFrame myFrame;

    Container contentPane; // 컨텐트팬
    SettingDialog settingDialog; // 설정창
    SharedMemory sharedMemory; // 공유 메모리
    ProducerThread producerThread; // 생산자 스레드
    ConsumerThread consumerThread; // 소비자 스레드

    // 패널
    JPanel menuPanel = new JPanel(); // 메뉴
    JPanel titlePanel = new JPanel(); // 제목
    JPanel producePanel = new JPanel(); // 생산 공간
    JPanel bufferPanel = new JPanel(); // 버퍼 공간
    JPanel consumePanel = new JPanel(); // 소비 공간

    // 스크롤팬
    JScrollPane produceScroll; // 생산 스크롤
    JScrollPane bufferScroll; // 버퍼 스크롤
    JScrollPane consumeScroll; // 소비 스크롤

    JButton [] menuButton = new JButton[3]; // 메뉴 버튼 3개
    String [] menuText = {"START", "INITIALIZATION", "SETTING"}; // 텍스트를 배열에 넣어두고 반복문으로 버튼 이름 넣어주려고 함
    JLabel [] titleLabel = new JLabel[3]; // 제목 버튼 3개
    String [] titleText = {"Producer", "Bounded Buffer", "Consumer"}; // 텍스트를 배열에 넣어두고 반복문으로 제목 이름 넣어주려고 함

    int bufferSize; // 버퍼 크기
    int equationNumber; // 사칙연산 개수

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

        // menu 컴포넌트들 생성
        for(int i=0; i < menuButton.length; i++){
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

        // producer, buffer, consume 화면 붙이기
        addNewScreen();

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

    // producer, buffer, consume 화면 붙이기 함수
    void addNewScreen(){
        // 패널 새로 생성
        producePanel = new JPanel();
        bufferPanel = new JPanel();
        consumePanel = new JPanel();

        // 레이아웃 설정
        producePanel.setLayout(new BoxLayout(producePanel, BoxLayout.Y_AXIS));
        bufferPanel.setLayout(new BoxLayout(bufferPanel, BoxLayout.Y_AXIS));
        consumePanel.setLayout(new BoxLayout(consumePanel, BoxLayout.Y_AXIS));

        // 스크롤팬 생성
        produceScroll = new JScrollPane(producePanel);
        bufferScroll = new JScrollPane(bufferPanel);
        consumeScroll = new JScrollPane(consumePanel);

        // 스크롤팬 사이즈 설정
        produceScroll.setPreferredSize(new Dimension(300, 400));
        consumeScroll.setPreferredSize(new Dimension(300, 400));
        bufferScroll.setPreferredSize(new Dimension(300, 400));

        // 스크롤팬을 컨텐트팬에 붙이기
        contentPane.add(produceScroll, BorderLayout.WEST); // 서쪽에 배치
        contentPane.add(bufferScroll, BorderLayout.CENTER); // 중앙에 배치
        contentPane.add(consumeScroll, BorderLayout.EAST); // 동쪽에 배치
    }

    // 이벤트 리스너
    // start 리스너
    class StartActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            producerThread.start(); // 생산자 스레드 시작
            consumerThread.start(); // 소비자 스레드 시작
        }
    }
    // 초기화 리스너
    class InitializationActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            // producer, buffer, consumer 화면 새로 붙이기
            addNewScreen();
            // 자식 컴포넌트 재배치
            contentPane.revalidate();
        }
    }
    // setting 리스너
    class SettingActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            // 다이얼로그 보이게 하기
            settingDialog.setVisible(true);

            // producer, buffer, consumer 화면 새로 붙이기
            addNewScreen();

            // 설정창에서 bufferSize와 equationNumber 받아와서 변수에 설정해주기
            bufferSize = Integer.parseInt(settingDialog.getInputBoundedBufferSize()); // bufferSize 받아오는 함수 호출
            equationNumber = Integer.parseInt(settingDialog.getInputEquationNumber()); // equationNumber 받아오는 함수 호출

            // buffer size만큼 공간 만들기
            bufferBox = new JLabel[bufferSize];
            for(int i=0; i<bufferSize; i++){
                // 번호 출력해주기
                bufferBox[i] = new JLabel("(" + (i+1) + ") ");
                // 배경색이 출력되도록 불투명성 설정
                bufferBox[i].setOpaque(true);
                // 폰트 설정
                bufferBox[i].setFont(new Font("Arial", Font.PLAIN, 20));
                // 패널에 붙이기
                bufferPanel.add(bufferBox[i]);
            }
            // equation 개수만큼 공간 만들기
            produceBox = new JLabel[equationNumber];
            consumeBox = new JLabel[equationNumber];
            for(int i=0; i<equationNumber; i++){
                // 번호 출력해주기
                produceBox[i] = new JLabel("(" + (i+1) + ") ");
                consumeBox[i] = new JLabel("(" + (i+1) + ") ");
                // 배경색이 출력되도록 불투명성 설정
                produceBox[i].setOpaque(true);
                consumeBox[i].setOpaque(true);
                // 폰트 설정
                produceBox[i].setFont(new Font("Arial", Font.PLAIN, 20));
                consumeBox[i].setFont(new Font("Arial", Font.PLAIN, 20));
                // 패널에 붙이기
                producePanel.add(produceBox[i]);
                consumePanel.add(consumeBox[i]);
            }

            // 공유메모리, 생산자, 소비자 스레드 생성
            // 여기서 myFrame을 매개변수로 전달해주기 위해서 myFrame을 static 변수로 선언해놓음
            sharedMemory = new SharedMemory(myFrame, equationNumber, bufferSize, bufferBox);
            producerThread = new ProducerThread(myFrame, sharedMemory, produceBox);
            consumerThread = new ConsumerThread(myFrame, sharedMemory, consumeBox);

            // 자식 컴포넌트 다시 배치
            contentPane.revalidate();
        }
    }

    public static void main(String[] args){
        // myFrame 생성하면서 프로그램 시작
        myFrame = new MyFrame();
    }
}
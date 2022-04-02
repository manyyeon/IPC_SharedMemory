package com.company;

import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.*;

// 공유 메모리
class SharedMemory{
    int in = 0;
    int out = 0;
    String [][] buffer; // 버퍼
    int equationNumber; // 계산할 사칙연산 개수
    int bufferSize;
    String [] consumingProblem; // 지금 계산하고 있는 문제

    // 생성자에서 버퍼, 사칙연산 개수 초기화
    SharedMemory(int equationNumber, int bufferSize){
        this.equationNumber = equationNumber;
        this.bufferSize = bufferSize;
        buffer = new String[this.bufferSize][];
    }

    synchronized void produce(String [] problem){
        if((in+1) % bufferSize == out){
            try{
                wait();
            }
            catch(InterruptedException e){
                return;
            }
        }
        buffer[in] = new String[problem.length];
        for(int i=0; i<problem.length; i++){
            buffer[in][i] = problem[i];
        }
        //printBuffer();
        in = (in+1) % bufferSize;
        notify();
    }

    synchronized void consume(){
        if(in == out){
            try{
                wait();
            } catch(InterruptedException e){
                return;
            }
        }
        consumingProblem = buffer[out];

        out = (out+1) % bufferSize;
        notify();
    }
}

// 생산자 스레드 - 사칙연산 랜덤 생성
class ProducerThread extends Thread {
    SharedMemory sharedMemory;
    String [] producingProblem; // 계산할 수식
    int termNum; // 항 개수
    int num; // 1~100 사이의 랜덤 숫자
    int tmpOperator; // 연산자 결정하는 숫자(1~4) - 1:+, 2:-, 3:*, 4:/
    String operator; // 연산자

    // 생성자로 변수 초기화
    ProducerThread(SharedMemory sharedMemory){
        // 공유 메모리 가져오기
        this.sharedMemory = sharedMemory;
        operator = "";
    }

    // 사칙연산 하나 랜덤 생성
    public void produceProblem(){
        termNum = (int)(Math.random()*4) + 3;
        producingProblem = new String[termNum*2-1];

        for(int i = 0; i< producingProblem.length; i+=2){
            // 1~100 숫자 랜덤 생성
            num = (int)(Math.random()*100) + 1;
            // 수식에 숫자 연결
            producingProblem[i] = Integer.toString(num);
            // 연산자 결정
            if(i != producingProblem.length-1){ // 마지막 항이 아닐 때만 연산자 생성
                tmpOperator = (int)(Math.random()*4) + 1;
                switch (tmpOperator){
                    case 1:
                        operator = "+"; break;
                    case 2:
                        operator = "-"; break;
                    case 3:
                        operator = "*"; break;
                    case 4:
                        operator = "/"; break;
                }
                // 수식에 연산자 연결
                producingProblem[i+1] = operator;
            }
        }
        synchronized (this) {
            System.out.print("넘겨주기 전 : ");
            for (int i = 0; i < producingProblem.length; i++) {
                System.out.print(producingProblem[i] + " ");
            }
            System.out.println();
        }
    }

    @Override
    public void run() {
        for(int i = 0; i<sharedMemory.equationNumber; i++) {
            try {
                sleep(200); // 오류 안나게 하려고 넣어놓은 것
                produceProblem(); // 사칙연산 하나 생성
                sharedMemory.produce(producingProblem);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

// 소비자 스레드 - 사칙연산 계산
class ConsumerThread extends Thread {
    SharedMemory sharedMemory;
    static Stack<Integer> intStack; // 숫자 스택
    static Stack<String> strStack; // 연산자 스택

    String symbol = "";

    public ConsumerThread(SharedMemory sharedMemory){
        // 공유 메모리 가져오기
        this.sharedMemory = sharedMemory;
        //sharedMemory.buffer[]
    }

    // 우선순위 반환 함수
    public static int getPriority(String operator){
        switch(operator){
            case "+":
            case "-":
                return 0;
            case "*":
            case "/":
                return 1;
        }
        return 0;
    }

    // 계산
    public static void calc(){
        int num2 = intStack.pop();
        int num1 = intStack.pop();
        String op = strStack.pop();

        switch(op){
            case "+":
                intStack.push(num1+num2); break;
            case "-":
                intStack.push(num1-num2); break;
            case "*":
                intStack.push(num1*num2); break;
            case "/":
                intStack.push(num1/num2); break;
        }
    }

    public void consumeProblem(){
        strStack = new Stack<String>();
        intStack = new Stack<Integer>();

        for(int i = 0; i< sharedMemory.consumingProblem.length; i++){
            symbol = sharedMemory.consumingProblem[i];
            if(symbol != "+" && symbol != "-" && symbol != "*" && symbol != "/"){
                intStack.push(Integer.parseInt(symbol));
            }
            else{
                while(!strStack.isEmpty() && getPriority(symbol) <= getPriority(strStack.peek())){
                    calc();
                }
                strStack.push(symbol);
            }
        }
        while(!strStack.isEmpty()){
            calc();
        }
        synchronized(this){
            System.out.print("\t\t\t\t\t\t\t\t" + "답 : ");
            for(int i = 0; i<sharedMemory.consumingProblem.length; i++){
                System.out.print(sharedMemory.consumingProblem[i] + " ");
            }
            System.out.println(" = " + intStack.peek());
        }
    }

    @Override
    public void run() {
        for(int i = 0; i<sharedMemory.equationNumber; i++){
            try{
                sleep(1000); // 오류 안나게 하려고 넣어놓은 것
                sharedMemory.consume();
                consumeProblem();
            } catch(InterruptedException e){
                return;
            }
        }
    }
}

public class MyFrame extends JFrame {
    Container contentPane;
    SettingDialog settingDialog;
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
    class StartActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.out.println("start");
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
            for(int i=0; i<bufferSize; i++){
                JButton bufferBox = new JButton("(" + (i+1) + ") " + "buffer");
                bufferBox.setPreferredSize(new Dimension(300, 200));
                bufferPanel.add(bufferBox);
            }
            // equation 개수만큼 공간 만들기
            for(int i=0; i<equationNumber; i++){
                JButton produceBox = new JButton("(" + (i+1) + ") " + "produce");
                JButton consumeBox = new JButton("(" + (i+1) + ") " + "consume");
                produceBox.setPreferredSize(new Dimension(300, 200));
                consumeBox.setPreferredSize(new Dimension(300, 200));
                producePanel.add(produceBox);
                consumePanel.add(consumeBox);
            }
            contentPane.revalidate(); // 자식 컴포넌트 다시 배치
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
                System.out.println("확인");
                setVisible(false);
            }
        });
    }

    public String getInputBoundedBufferSize() {
        if(inputBoundedBufferSize.getText().length() == 0){
            return null;
        } else{
            return inputBoundedBufferSize.getText();
        }
    }
    public String getInputEquationNumber() {
        if(inputEquationNumber.getText().length() == 0){
            return null;
        } else{
            return inputEquationNumber.getText();
        }
    }
}
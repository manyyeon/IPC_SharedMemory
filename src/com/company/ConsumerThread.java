package com.company;

import javax.swing.*;
import java.util.Stack;
import java.awt.*;

// 소비자 스레드 - 사칙연산 계산
class ConsumerThread extends Thread {
    MyFrame myFrame; // 화면
    SharedMemory sharedMemory; // 공유 메모리
    JPanel [] consumeOuterBox; // consumBox를 담는 공간
    JLabel [] consumeBox; // 식 소비 공간

    String [] consumingProblem; // 계산하는 수식
    double ans; // 답

    static Stack<Double> doubleStack; // 숫자 스택
    static Stack<String> strStack; // 연산자 스택

    // 자동 스크롤을 구현하기 위한 변수
    // scrollLength 값을 올려주면서 스크롤 위치값에 설정해줌
    int scrollLength = 0;

    String showConsumeProblem = ""; // 현재 계산한 사칙연산을 띄워주기 위한 문자열 변수

    public ConsumerThread(MyFrame myFrame, SharedMemory sharedMemory, JLabel [] consumeBox, JPanel [] consumeOuterBox){
        this.myFrame = myFrame; // 화면 가져오기
        this.sharedMemory = sharedMemory; // 공유 메모리 가져오기
        this.consumeBox = consumeBox; // 계산결과 띄워주는 공간 가져오기
        this.consumeOuterBox = consumeOuterBox;
    }

    // 우선순위 반환 함수
    // +, -면 0, *, /면 1을 반환
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

    // 스택에 있는 두 숫자를 연산자에 따라 계산해주는 함수
    public static void calc(){
        // 스택에서 두 숫자 꺼내기
        double num2 = doubleStack.pop();
        double num1 = doubleStack.pop();
        // 연산자 꺼내기
        String op = strStack.pop();

        // 연산자에 따라서 계산하고 다시 숫자 스택에 넣기
        switch(op){
            case "+":
                doubleStack.push(num1+num2); break;
            case "-":
                doubleStack.push(num1-num2); break;
            case "*":
                doubleStack.push(num1*num2); break;
            case "/":
                doubleStack.push(num1/num2); break;
        }
    }

    // 사칙연산 계산하는 함수
    public void consumeProblem(){
        strStack = new Stack<String>(); // 연산자 스택 생성
        doubleStack = new Stack<Double>(); // 숫자 스택 생성
        String symbol = ""; // 버퍼에서 가져온 사칙연산에서 꺼낸 것(연산자이거나 숫자)

        for(int i = 0; i< consumingProblem.length; i++){
            symbol = consumingProblem[i]; // 버퍼에서 가져온 사칙연산에서 일단 하나 꺼내기
            // symbol이 숫자이면
            if(symbol != "+" && symbol != "-" && symbol != "*" && symbol != "/"){
                // 숫자 스택에 넣기
                doubleStack.push(Double.parseDouble(symbol));
            }
            // symbol이 연산자이면
            else{
                // 연산자 스택이 비거나 현재 연산자가 연산자 스택 맨 위에 있는 연산자의 우선순위보다 높을 때까지
                while(!strStack.isEmpty() && getPriority(symbol) <= getPriority(strStack.peek())){
                    // 스택에 있는 두 숫자를 꺼내서 계산
                    calc();
                }
                strStack.push(symbol);
            }
        }
        while(!strStack.isEmpty()){
            calc();
        }
        ans = doubleStack.peek();
    }

    // 스레드 코드
    // start() 메소드가 호출된 후 스레드가 실행을 시작하는 메소드
    // 이 메소드가 종료하면 스레드도 종료됨
    @Override
    public void run() {
        // 자동 스크롤을 구현하기 위한 변수
        // scrollLength 값을 올려주면서 스크롤 위치값에 설정해줌
        scrollLength = 0;
        // 설정된 식 개수만큼 다 계산할 때까지 반복
        for(int i = 0; i<sharedMemory.equationNumber; i++) {
            try {
                sleep(10); // 시간 지연
                // 공유 메모리 버퍼에서 계산할 식 가져오기
                sharedMemory.consume();
                consumingProblem = sharedMemory.consumingProblem;
                // 사칙연산 계산하는 함수 호출
                consumeProblem();
                // 화면의 consume 부분에 계산결과 띄워주기
                showConsumeProblem = "";
                showConsumeProblem += "(";
                showConsumeProblem += i+1; // 몇번째 계산결과인지 표시
                showConsumeProblem += ") ";
                // 받아온 식을 tmpProblem에 연결
                for (int j = 0; j < consumingProblem.length; j++) {
                    showConsumeProblem += consumingProblem[j];
                }
                // 계산 결과도 연결
                showConsumeProblem += " = ";
                showConsumeProblem += ans;
                // 소비 공간 해당 번호 문자열을 tmpProblem으로 변경
                consumeBox[i].setText(showConsumeProblem);

                // 배경 색 변경(계산 다 끝낸 식 초록색으로 칠해주기)
                consumeOuterBox[i].setBackground(new Color(0,255,0));
                //consumeBox[i].setBackground(new Color(0, 255, 0));

                if(i>=10){
                    // 자동 스크롤
                    scrollLength += (myFrame.consumeScroll.getVerticalScrollBar().getMaximum() - myFrame.consumeScroll.getVerticalScrollBar().getMinimum()) / sharedMemory.equationNumber;
                    myFrame.consumeScroll.getVerticalScrollBar().setValue(myFrame.consumeScroll.getVerticalScrollBar().getMinimum() + scrollLength);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        // 모든 계산 결과 출력이 끝나면 결과 창을 띄워줌
        ResultDialog resultDialog = new ResultDialog(myFrame, "결과", sharedMemory.bufferSize, sharedMemory.equationNumber); // 결과 창 객체 생성
        resultDialog.setVisible(true); // 결과 창 보이게 하기
    }
}

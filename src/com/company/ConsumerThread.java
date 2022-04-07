package com.company;

import javax.swing.*;
import java.util.Stack;
import java.awt.*;

// 소비자 스레드 - 사칙연산 계산
class ConsumerThread extends Thread {
    MyFrame myFrame; // 화면
    SharedMemory sharedMemory; // 공유 메모리
    JLabel [] consumeBox; // 식 소비 공간

    String [] consumingProblem; // 계산하는 수식
    int ans; // 답

    static Stack<Integer> intStack; // 숫자 스택
    static Stack<String> strStack; // 연산자 스택

    String symbol = "";

    public ConsumerThread(MyFrame myFrame, SharedMemory sharedMemory, JLabel [] consumeBox){
        this.myFrame = myFrame;
        // 공유 메모리 가져오기
        this.sharedMemory = sharedMemory;
        //sharedMemory.buffer[]
        this.consumeBox = consumeBox;
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

        for(int i = 0; i< consumingProblem.length; i++){
            symbol = consumingProblem[i];
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
        ans = intStack.peek();
//        synchronized(this){
//            System.out.print("\t\t\t\t\t\t\t\t" + "답 : ");
//            for(int i = 0; i < consumingProblem.length; i++){
//                System.out.print(consumingProblem[i] + " ");
//            }
//            System.out.println(" = " + intStack.peek());
//        }
    }

    @Override
    public void run() {
        int scrollLength = 0;
        for(int i = 0; i<sharedMemory.equationNumber; i++){
            try{
                sleep(100); // 오류 안나게 하려고 넣어놓은 것
                consumingProblem = sharedMemory.consume();
                consumeProblem();
                // 화면의 consume 부분에 계산결과 띄워주기
                String tmpProblem = "";
                for(int j=0; j < consumingProblem.length; j++){
                    tmpProblem += consumingProblem[j];
                }
                tmpProblem += " = ";
                tmpProblem += ans;
                consumeBox[i].setText(tmpProblem);

                // 배경 색 변경
                consumeBox[i].setBackground(new Color(0,255,0));

                // 자동 스크롤
                scrollLength += 12;
                myFrame.consumeScroll.getVerticalScrollBar().setValue(myFrame.consumeScroll.getVerticalScrollBar().getMinimum() + scrollLength);
            } catch(InterruptedException e){
                return;
            }
        }
    }
}

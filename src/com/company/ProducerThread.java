package com.company;

import javax.swing.*;
import java.awt.*;

// 생산자 스레드 - 사칙연산 랜덤 생성
class ProducerThread extends Thread {
    MyFrame myFrame; // 화면
    SharedMemory sharedMemory; // 공유 메모리
    JPanel [] produceOuterBox; // produceBox를 담는 공간
    JLabel [] produceBox; // 식 생산 공간

    String [] producingProblem; // 계산할 수식
    int termNum; // 항 개수
    int num; // 1~100 사이의 랜덤 숫자
    int tmpOperator; // 연산자 결정하는 숫자(1~4) - 1:+, 2:-, 3:*, 4:/
    String operator = ""; // 연산자

    // 자동 스크롤을 구현하기 위한 변수
    // scrollLength 값을 올려주면서 스크롤 위치값에 설정해줌
    int scrollLength = 0;
    String showProduceProblem = ""; // 현재 만든 사칙연산을 띄워주기 위한 문자열 변수

    // 생성자로 변수 초기화
    ProducerThread(MyFrame myFrame, SharedMemory sharedMemory, JLabel [] produceBox, JPanel [] produceOuterBox){
        // 화면 가져오기
        this.myFrame = myFrame;
        // 공유 메모리 가져오기
        this.sharedMemory = sharedMemory;
        // 식 생산 공간 가져오기
        this.produceBox = produceBox;
        this.produceOuterBox = produceOuterBox;
    }

    // 사칙연산 하나 랜덤 생성
    public void produceProblem(){
        // 항 개수 랜덤으로 설정하기
        termNum = (int)(Math.random()*4) + 3;
        // 항 개수와 연산자 개수만큼 생산할 사칙연산 공간 생성
        producingProblem = new String[termNum*2-1];

        // 사칙연산 숫자와 연산자 랜덤으로 뽑아서 연결해주기
        for(int i = 0; i< producingProblem.length; i+=2){
            // 1~100 숫자 랜덤 생성
            num = (int)(Math.random()*100) + 1;
            // 수식에 숫자 연결
            producingProblem[i] = Integer.toString(num);
            // 연산자 랜덤으로 설정
            if(i != producingProblem.length-1){ // 마지막 항이 아닐 때만 연산자 생성
                // 연산자 랜덤으로 뽑기
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
    }

    // 스레드 코드
    // start() 메소드가 호출된 후 스레드가 실행을 시작하는 메소드
    // 이 메소드가 종료하면 스레드도 종료됨
    @Override
    public void run() {
        // 자동 스크롤을 구현하기 위한 변수
        // scrollLength 값을 올려주면서 스크롤 위치값에 설정해줌
        scrollLength = 0;
        // 설정된 식 개수만큼 다 만들 때까지 반복
        for(int i = 0; i<sharedMemory.equationNumber; i++) {
            try {
                sleep(20); // 시간 지연
                produceProblem(); // 사칙연산 하나 생성
                // 화면의 produce 부분에 생성한 식 띄워주기
                // 몇번째 식인지 번호로 표시
                showProduceProblem = "";
                showProduceProblem += "(";
                showProduceProblem += i+1; // 몇번째 사칙연산 생성인건지 표시
                showProduceProblem += ") ";
                // 생산한 랜덤 식을 tmpProblem에 연결
                for(int j=0; j < producingProblem.length; j++){
                    showProduceProblem += producingProblem[j];
                }
                // 생산 공간 해당 번호 문자열을 tmpProblem으로 변경
                produceBox[i].setText(showProduceProblem);

                // 배경 색 변경(생산한 식 초록색으로 칠해주기)
                produceOuterBox[i].setBackground(new Color(0,255, 0));
                //produceBox[i].setBackground(new Color(0,255,0));

                if(i>=10){
                    // 자동 스크롤
                    scrollLength += (myFrame.produceScroll.getVerticalScrollBar().getMaximum() - myFrame.produceScroll.getVerticalScrollBar().getMinimum()) / sharedMemory.equationNumber;
                    myFrame.produceScroll.getVerticalScrollBar().setValue(myFrame.produceScroll.getVerticalScrollBar().getMinimum() + scrollLength);
                }

                // 공유 메모리에 여기서 만든 사칙연산 전달
                sharedMemory.produce(producingProblem);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

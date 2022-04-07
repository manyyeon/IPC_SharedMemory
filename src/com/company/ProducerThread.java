package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// 생산자 스레드 - 사칙연산 랜덤 생성
class ProducerThread extends Thread {
    MyFrame myFrame; // 화면
    SharedMemory sharedMemory; // 공유 메모리
    JLabel [] produceBox; // 식 생산 공간

    String [] producingProblem; // 계산할 수식
    int termNum; // 항 개수
    int num; // 1~100 사이의 랜덤 숫자
    int tmpOperator; // 연산자 결정하는 숫자(1~4) - 1:+, 2:-, 3:*, 4:/
    String operator; // 연산자

    // 생성자로 변수 초기화
    ProducerThread(MyFrame myFrame, SharedMemory sharedMemory, JLabel [] produceBox){
        this.myFrame = myFrame;
        // 공유 메모리 가져오기
        this.sharedMemory = sharedMemory;
        this.produceBox = produceBox;
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

//        synchronized (this) {
//            System.out.print("넘겨주기 전 : ");
//            for (int i = 0; i < producingProblem.length; i++) {
//                System.out.print(producingProblem[i] + " ");
//            }
//            System.out.println();
//        }
    }

    @Override
    public void run() {
        for(int i = 0; i<sharedMemory.equationNumber; i++) {
            try {
                sleep(200); // 오류 안나게 하려고 넣어놓은 것
                produceProblem(); // 사칙연산 하나 생성
                String tmpProblem = "";
                for(int j=0; j < producingProblem.length; j++){
                    tmpProblem += producingProblem[j];
                }
                produceBox[i].setText(tmpProblem);
                sharedMemory.produce(producingProblem);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

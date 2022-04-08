package com.company;

import javax.swing.*;
import java.awt.*;

// 공유 메모리
class SharedMemory{
    MyFrame myFrame;
    int in = 0;
    int out = 0;

    JLabel[] bufferBox; // 버퍼 공간
    String [][] buffer; // 버퍼
    int equationNumber; // 계산할 사칙연산 개수
    int bufferSize;

    // 생성자에서 버퍼, 사칙연산 개수 초기화
    SharedMemory(MyFrame myFrame, int equationNumber, int bufferSize, JLabel [] bufferBox){
        this.myFrame = myFrame;
        this.equationNumber = equationNumber;
        this.bufferSize = bufferSize;
        this.bufferBox = bufferBox;
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
        String tmpProblem = "";
        for(int i=0; i<problem.length; i++){
            buffer[in][i] = problem[i];
            tmpProblem += problem[i];
        }
        bufferBox[in].setText(tmpProblem);

        bufferBox[in].setBackground(new Color(237,237,237));
        in = (in+1) % bufferSize;
        bufferBox[in].setBackground(new Color(0,255,0));
        notify();
    }

    synchronized String [] consume(){
        if(in == out){
            try{
                bufferBox[in].setBackground(new Color(0,0,255));
                wait();
            } catch(InterruptedException e){
                System.out.println("");
            }
        }
        String [] returnProblem = buffer[out];

        bufferBox[out].setBackground(new Color(237,237,237));
        out = (out+1) % bufferSize;
        bufferBox[out].setBackground(new Color(255,0,0));
        notify();
        return returnProblem;
    }
}

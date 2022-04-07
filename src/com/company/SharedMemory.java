package com.company;

// 공유 메모리
class SharedMemory{
    MyFrame myFrame;
    int in = 0;
    int out = 0;
    String [][] buffer; // 버퍼
    int equationNumber; // 계산할 사칙연산 개수
    int bufferSize;
    //String [] consumingProblem; // 지금 계산하고 있는 문제

    // 생성자에서 버퍼, 사칙연산 개수 초기화
    SharedMemory(MyFrame myFrame, int equationNumber, int bufferSize){
        this.myFrame = myFrame;
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

    synchronized String [] consume(){
        if(in == out){
            try{
                wait();
            } catch(InterruptedException e){
                System.out.println("");
            }
        }
        String [] returnProblem = buffer[out];

        out = (out+1) % bufferSize;
        notify();
        return returnProblem;
    }
}

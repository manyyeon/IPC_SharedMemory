package com.company;

// 공유 메모리
class SharedMemory{
    int in = 0;
    int out = 0;
    String [] buffer;
    int bufferSize = 4;

    // 생성자에서 버퍼 초기화
    SharedMemory(){
        buffer = new String[bufferSize];
    }

    synchronized void produce(String problem){
        if((in+1) % bufferSize == out){
            try{
                wait();
            }
            catch(InterruptedException e){
                return;
            }
            buffer[in] = problem;
            in++;
            notify();
        }
    }

    synchronized void consume(){
        if(in == out){
            try{
                wait();
            } catch(InterruptedException e){
                return;
            }
            notify();
        }
    }
}

// 생산자 스레드 - 사칙연산 랜덤 생성
class ProducerThread extends Thread {
    SharedMemory sharedMemory;
    String problem; // 계산할 수식
    int termNum; // 항 개수
    int num; // 1~100 사이의 랜덤 숫자
    int tmpOperator; // 연산자 결정하는 숫자(1~4) - 1:+, 2:-, 3:*, 4:/
    String operator; // 연산자

    // 생성자로 변수 초기화
    ProducerThread(SharedMemory sharedMemory){
        // 공유 메모리 가져오기
        this.sharedMemory = sharedMemory;
        problem = "";
        termNum = (int)(Math.random()*3) + 3;
        operator = "";
    }

    // 사칙연산 랜덤 생성
    public void produceProblem(){
        for(int i=0; i<termNum; i++){
            // 1~100 숫자 랜덤 생성
            num = (int)(Math.random()*100) + 1;
            // 수식에 숫자 연결
            problem += num;
            // 연산자 결정
            if(i != termNum-1){ // 마지막 항이 아닐 때만 연산자 생성
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
                problem += operator;
            }
        }
    }

    @Override
    public void run() {
        while(true) {
        try {
            produceProblem();
            sharedMemory.produce(problem);
            wait(); // 오류 안나게 하려고 넣어놓은 것
        } catch (InterruptedException e) {
            return;
        }
    } }
}

// 소비자 스레드 - 사칙연산 계산
class ConsumerThread extends Thread {
    SharedMemory sharedMemory;

    public ConsumerThread(SharedMemory sharedMemory){
        // 공유 메모리 가져오기
        this.sharedMemory = sharedMemory;
    }

    @Override
    public void run() {

    }
}

public class IPC_sharedMemory {

    public static void main(String[] args) {
        SharedMemory sm = new SharedMemory();
        ProducerThread pt = new ProducerThread(sm);
        ConsumerThread ct = new ConsumerThread(sm);

        pt.start();
        ct.start();
    }
}

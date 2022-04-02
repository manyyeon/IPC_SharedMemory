//package com.company;
//
//import java.util.Scanner;
//import java.util.Stack;
//
//// 공유 메모리
//class SharedMemory{
//    int in = 0;
//    int out = 0;
//    String [][] buffer;
//    int equationNumber; // 계산할 사칙연산 개수
//    int bufferSize;
//    String [] consumingProblem;
//
//    // 생성자에서 버퍼, 사칙연산 개수 초기화
//    SharedMemory(int equationNumber, int bufferSize){
//        this.equationNumber = equationNumber;
//        this.bufferSize = bufferSize;
//        buffer = new String[this.bufferSize][];
//    }
//
//    synchronized void printBuffer(){
//        for(int i=0; i<bufferSize; i++){
//            try{
//                for(int j=0; j<buffer[i].length; j++){
//                    System.out.print(buffer[i][j] + " ");
//                }
//            } catch(NullPointerException e){
//                System.out.print("null ");
//            }
//            finally {
//                System.out.println("\n----------");
//            }
//        }
//    }
//
//    synchronized void produce(String [] problem){
//        if((in+1) % bufferSize == out){
//            try{
//                wait();
//            }
//            catch(InterruptedException e){
//                return;
//            }
//        }
//        buffer[in] = new String[problem.length];
//        for(int i=0; i<problem.length; i++){
//            buffer[in][i] = problem[i];
//        }
//        synchronized (this){
//            printBuffer();
//        }
//        in = (in+1) % bufferSize;
//        notify();
//    }
//
//    synchronized void consume(){
//        if(in == out){
//            try{
//                wait();
//            } catch(InterruptedException e){
//                return;
//            }
//        }
//        consumingProblem = buffer[out];
//
//        out = (out+1) % bufferSize;
//        notify();
//    }
//}
//
//// 생산자 스레드 - 사칙연산 랜덤 생성
//class ProducerThread extends Thread {
//    SharedMemory sharedMemory;
//    String [] problem; // 계산할 수식
//    int termNum; // 항 개수
//    int num; // 1~100 사이의 랜덤 숫자
//    int tmpOperator; // 연산자 결정하는 숫자(1~4) - 1:+, 2:-, 3:*, 4:/
//    String operator; // 연산자
//
//    // 생성자로 변수 초기화
//    ProducerThread(SharedMemory sharedMemory){
//        // 공유 메모리 가져오기
//        this.sharedMemory = sharedMemory;
//        operator = "";
//    }
//
//    // 사칙연산 하나 랜덤 생성
//    public void produceProblem(){
//        termNum = (int)(Math.random()*4) + 3;
//        problem = new String[termNum*2-1];
//
//        for(int i=0; i<problem.length; i+=2){
//            // 1~100 숫자 랜덤 생성
//            num = (int)(Math.random()*100) + 1;
//            // 수식에 숫자 연결
//            problem[i] = Integer.toString(num);
//            // 연산자 결정
//            if(i != problem.length-1){ // 마지막 항이 아닐 때만 연산자 생성
//                tmpOperator = (int)(Math.random()*4) + 1;
//                switch (tmpOperator){
//                    case 1:
//                        operator = "+"; break;
//                    case 2:
//                        operator = "-"; break;
//                    case 3:
//                        operator = "*"; break;
//                    case 4:
//                        operator = "/"; break;
//                }
//                // 수식에 연산자 연결
//                problem[i+1] = operator;
//            }
//        }
//        synchronized (this) {
//            System.out.print("넘겨주기 전 : ");
//            for (int i = 0; i < problem.length; i++) {
//                System.out.print(problem[i] + " ");
//            }
//            System.out.println();
//        }
//    }
//
//    @Override
//    public void run() {
//        for(int i = 0; i<sharedMemory.equationNumber; i++) {
//            try {
//                sleep(200); // 오류 안나게 하려고 넣어놓은 것
//                produceProblem(); // 사칙연산 하나 생성
//                sharedMemory.produce(problem);
//            } catch (InterruptedException e) {
//                return;
//            }
//        }
//    }
//}
//
//// 소비자 스레드 - 사칙연산 계산
//class ConsumerThread extends Thread {
//    SharedMemory sharedMemory;
//    static Stack<Integer> intStack; // 숫자 스택
//    static Stack<String> strStack; // 연산자 스택
//
//    String symbol = "";
//
//    public ConsumerThread(SharedMemory sharedMemory){
//        // 공유 메모리 가져오기
//        this.sharedMemory = sharedMemory;
//        //sharedMemory.buffer[]
//    }
//
//    // 우선순위 반환 함수
//    public static int getPriority(String operator){
//        switch(operator){
//            case "+":
//            case "-":
//                return 0;
//            case "*":
//            case "/":
//                return 1;
//        }
//        return 0;
//    }
//
//    // 계산
//    public static void calc(){
//        int num2 = intStack.pop();
//        int num1 = intStack.pop();
//        String op = strStack.pop();
//
//        switch(op){
//            case "+":
//                intStack.push(num1+num2); break;
//            case "-":
//                intStack.push(num1-num2); break;
//            case "*":
//                intStack.push(num1*num2); break;
//            case "/":
//                intStack.push(num1/num2); break;
//        }
//    }
//
//    public void consumeProblem(){
//        strStack = new Stack<String>();
//        intStack = new Stack<Integer>();
//
//        for(int i = 0; i< sharedMemory.consumingProblem.length; i++){
//            symbol = sharedMemory.consumingProblem[i];
//            if(symbol != "+" && symbol != "-" && symbol != "*" && symbol != "/"){
//                intStack.push(Integer.parseInt(symbol));
//            }
//            else{
//                while(!strStack.isEmpty() && getPriority(symbol) <= getPriority(strStack.peek())){
//                    calc();
//                }
//                strStack.push(symbol);
//            }
//        }
//        while(!strStack.isEmpty()){
//            calc();
//        }
//        synchronized(this){
//            System.out.print("\t\t\t\t\t\t\t\t" + "답 : ");
//            for(int i = 0; i<sharedMemory.consumingProblem.length; i++){
//                System.out.print(sharedMemory.consumingProblem[i] + " ");
//            }
//            System.out.println(" = " + intStack.peek());
//        }
//    }
//
//    @Override
//    public void run() {
//        for(int i = 0; i<sharedMemory.equationNumber; i++){
//            try{
//                sleep(1000); // 오류 안나게 하려고 넣어놓은 것
//                sharedMemory.consume();
//                consumeProblem();
//            } catch(InterruptedException e){
//                return;
//            }
//        }
//    }
//}
//
//public class IPC_sharedMemory {
//
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//
//        int calcNum; // 계산할 사칙연산 개수
//        int bufferSize; // 버퍼 사이즈
//
//        System.out.print("몇 개 계산?");
//        calcNum = sc.nextInt();
//        System.out.print("버퍼 몇 개?");
//        bufferSize = sc.nextInt();
//
//        SharedMemory sm = new SharedMemory(calcNum, bufferSize);
//        ProducerThread pt = new ProducerThread(sm);
//        ConsumerThread ct = new ConsumerThread(sm);
//
//        pt.start();
//        ct.start();
//    }
//}

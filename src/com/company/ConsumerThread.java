package com.company;

import java.util.Stack;

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

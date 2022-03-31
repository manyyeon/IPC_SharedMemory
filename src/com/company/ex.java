package com.company;

import java.util.Stack;

public class ex {
    static Stack<Integer> intStack;
    static Stack<String> strStack;

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
    public static void main(String args[]){
        String [] problem; // 계산할 수식
        int termNum; // 항 개수
        int num; // 1~100 사이의 랜덤 숫자
        int tmpOperator; // 연산자 결정하는 숫자(1~4) - 1:+, 2:-, 3:*, 4:/
        String operator; // 연산자

        termNum = (int)(Math.random()*4) + 3;
        problem = new String[termNum*2-1];
        operator = "";

        // 사칙연산 하나 랜덤 생성
        for (int i = 0; i < problem.length; i += 2) {
            // 1~100 숫자 랜덤 생성
            num = (int) (Math.random() * 100) + 1;
            // 수식에 숫자 연결
            problem[i] = Integer.toString(num);
            // 연산자 결정
            if (i != problem.length - 1) { // 마지막 항이 아닐 때만 연산자 생성
                tmpOperator = (int) (Math.random() * 4) + 1;
                switch (tmpOperator) {
                    case 1:
                        operator = "+";
                        break;
                    case 2:
                        operator = "-";
                        break;
                    case 3:
                        operator = "*";
                        break;
                    case 4:
                        operator = "/";
                        break;
                }
                // 수식에 연산자 연결
                problem[i + 1] = operator;
            }
        }

        for (int i = 0; i < problem.length; i++) {
            System.out.print(problem[i] + " ");
        }

        String symbol = "";
        strStack = new Stack<>();
        //Stack<Integer> intStack = new Stack<>();
        intStack = new Stack<>();

        for(int i=0; i< problem.length; i++){
            symbol = problem[i];
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
        System.out.println("\n답 : " + intStack.peek());
    }
}

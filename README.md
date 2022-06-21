# IPC_SharedMemory
운영체제 
## Shared Memory를 이용한 IPC 통신
구현언어 : java

# 목차
1. 전체적인 구조
2. 프로그램 동작 방식

# 1. 전체적인 구조
총 6개의 클래스로 구성되어 있다.
1)	SharedMemory
2)	ProducerThread
3)	ConsumerThread
4)	MyFrame
5)	SettingDialog
6)	ResultDialog

생산자, 소비자 스레드 클래스를 만들고 공유 메모리 클래스를 만든다. 그리고 MyFrame에서 GUI로 띄워줄 수 있도록 하고 SettingDialog 클래스에서 Bounded Buffer Size와 Equation 발생 횟수를 입력받는다.

## 각각의 구조 설명
## 1)	SharedMemory
```
String [][] buffer; // 버퍼
```
버퍼는 String형 2차원 배열을 사용해서 식을 저장해준다.
만약에 버퍼 사이즈가 4이고 사칙연산 4개가 이렇게 들어온다고 해보자.
```
(1)	3*14+51-11*2
(2)	24/2+4
(3)	32+51
(4)	19-9*3/2
```
우선, 숫자와 연산자로 쪼개고(근데 String형으로 유지) 2차원 배열에 공간을 필요한 만큼만 할당 받아서 아래 그림처럼 저장한다.

<img width="430" alt="image" src="https://user-images.githubusercontent.com/87538540/174727173-432271b7-49b9-4981-b6fb-1551edeaae19.png">

```
buffer[in] = new String[problem.length]; // 사칙연산 길이만큼 공간 할당
```
 
SharedMemory 클래스 안에 생산자 스레드가 버퍼에 접근하게 해주는 produce 함수와 소비자 스레드가 버퍼에 접근하게 해주는 consume 함수를 만들어서 생산자 스레드와 소비자 스레드를 관리한다.

### 스레드 관리
```
in : buffer에 producer가 식을 넣을 공간 인덱스
out : buffer에서 consumer가 식을 가져갈 공간 인덱스
```
in, out 변수를 활용하여 생산자 스레드는 버퍼가 꽉 찼을 때 소비자 스레드가 식을 가져가서 버퍼에 공간이 생길 때까지 기다리도록 하고 소비자 스레드는 버퍼가 비었을 때 생산자 스레드가 계산할 식을 버퍼에 넣어줄 때까지 기다리도록 한다.

#### 생산자 스레드 관리 알고리즘
```
synchronized void produce(String [] problem){
    // 버퍼가 꽉 찼을 때
    if((in+1) % bufferSize == out){
	// 버퍼에 공간이 생길 때까지 기다리기
		wait();
    }
		// 버퍼의 in이 가리키는 인덱스에 생산자 스레드가 만든 랜덤 사칙연산 저장
    // 랜덤 사칙연산을 화면에도 표시
    // in의 인덱스를 다음 공간을 가리키도록 변경
    in = (in+1) % bufferSize;

    // 다른 스레드 깨우기
    notify();
}
```

#### 소비자 스레드 관리 알고리즘
```
synchronized void consume(){
        // 버퍼가 비어있을 때
        if(in == out){
                // 버퍼에 사칙연산이 새로 들어올 때까지 대기
                wait();
        }
		// out의 인덱스를 다음 공간을 가리키도록 변경
        out = (out+1) % bufferSize; 

        // 다른 스레드 깨우기
        notify();

        // 전달해줄 사칙연산 설정
        consumingProblem = buffer[out];
    }
}
```

-------

## 2) ProducerThread
run 함수에서 사칙연산 개수만큼 반복문을 돌린다.
사칙연산을 랜덤으로 만들어서 sharedMemory의 produce 함수에 매개변수로 전달해준다.

------

## 3) ConsumerThread
run 함수에서 사칙연산 개수만큼 반복문을 돌린다.
consume 함수에서 사칙연산을 가져와서 계산하고 결과값을 출력해준다.

--------

## 4)	MyFrame
작동 과정을 보여줄 기본적인 화면을 구성한다.
```
JLabel [] bufferBox; // 버퍼 공간
JLabel [] produceBox; // 식 생산 공간
JLabel [] consumeBox; // 식 계산해서 답 보여주는 공간
```
버퍼에 있는 사칙연산들, 생산자 스레드가 생산하는 사칙연산들, 소비자 스레드가 계산한 사칙연산들을 JLabel의 텍스트로 설정해주고 화면에 보여준다.
그래서 버퍼 사이즈만큼 bufferBox 배열 공간을 만들고, 사칙연산 개수만큼 produceBox, consumeBox 공간을 만들었다.
ProducerThread, ConsumerThread, SharedMemory 객체를 생성할 때 객체 생산자 매개변수로 저 배열을 넘겨주고 그 객체 안에서 직접 텍스트를 변경할 수 있도록 하였다.

-----

## 5) SettingDialog
버퍼 사이즈와 사칙연산 개수를 입력받는다. 그리고 이 값들을 리턴해주는 함수를 만들어서 MyFrame에서 이 값들을 전달 받는다.

-----

## 6) ResultDialog
작동 결과를 출력해준다. 생성자에 매개변수로 버퍼 사이즈와 사칙연산 개수를 입력받는다. ConsumerThread에서 모든 계산이 끝나면 버퍼 사이즈와 사칙연산 개수를 넘겨주면서 이 객체를 생성한다.

# 2. 프로그램 동작 방식
## 1) GUI 구성

<img width="427" alt="image" src="https://user-images.githubusercontent.com/87538540/174727525-7321b640-e2be-4908-95d1-ff79e7929334.png">
 
## 2)	동작 과정
SETTING 버튼을 눌러서 Bounded Buffer 크기와 Equation 발생 횟수를 입력 받는다.

<img width="380" alt="image" src="https://user-images.githubusercontent.com/87538540/174727567-36cf08ca-0590-4bc2-b452-07c730a10407.png">
 
입력 받은 만큼 buffer 공간과 계산할 식을 출력해줄 공간을 만든다.

<img width="412" alt="image" src="https://user-images.githubusercontent.com/87538540/174727614-022ad506-716d-4608-97dd-4493c9787e98.png">

START 버튼을 눌러서 시작한다.
버퍼에서 in은 초록색, out은 빨간색, in == out일 때는 파란색으로 표시해준다.

<img width="396" alt="image" src="https://user-images.githubusercontent.com/87538540/174727639-0380e48c-ad5d-488f-98d4-e072bdb456ac.png">

<img width="396" alt="image" src="https://user-images.githubusercontent.com/87538540/174727830-4e6a9641-5ab2-46d1-9743-6e541764bcce.png">

<img width="359" alt="image" src="https://user-images.githubusercontent.com/87538540/174727839-9b4377b5-2e6c-460c-a5f2-c0a6a21ae26f.png">
   
계산이 끝나면 자동으로 결과 창을 띄워서 결과를 출력해준다.

<img width="358" alt="image" src="https://user-images.githubusercontent.com/87538540/174727872-c9407138-7f15-43f3-b616-919ee7f008f8.png">

INITIALIZATION 버튼을 눌러서 초기화해주면 새로 SETTING하고 실행할 수 있다.

<img width="367" alt="image" src="https://user-images.githubusercontent.com/87538540/174727889-344e9815-afd9-466c-a3b5-c124330aae15.png">


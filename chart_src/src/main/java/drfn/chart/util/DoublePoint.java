package drfn.chart.util;

public class DoublePoint {
    public double x; // double 형 x 변수 선언
    public double y; // double 형 y 변수 선언

    public DoublePoint() // 생성자함수
    {
        this(0,0); // 자신의 생성자에 0,0 값을 넘겨줌
    }

    public DoublePoint(double x, double y) // 생성자로 int 형 파라미터 2개를 받음
    {
        this.x = x; // 넘겨받은 첫번째 파라미터를 x 값에 적용
        this.y = y; // 넘겨받은 두번째 파라미터를 y 값에 적용
    }

    public String toString()
    {
        return "["+x+","+y+"]";
    }
}

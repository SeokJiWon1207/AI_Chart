package drfn.chart.event;
import java.util.EventListener;

public interface ChartChangedListener extends EventListener{
    public void addGraph(ChartEvent e);//인덱스 스크롤에 의해 바뀌었을때
    public void removeGraph(ChartEvent e);//한화면에 표시할 데이터의 수가 바뀌었을때
    public void initChart(ChartEvent e);//차트 초기화
    public void notifyChartAnalToolDone(ChartEvent e);//분석도구 그리기가 완료되었음을 알려준다
}
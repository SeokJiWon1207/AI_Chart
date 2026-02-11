package drfn.chart.model;
import java.util.*;

public class ChartRealPacketDataModel{
    String packet_title;   // 패킷 타이틀
    Vector<String> field_title;    // 필드 타이틀 
    Vector<String> packet_key;//코드
    public ChartRealPacketDataModel(String packet_title){
        this.packet_title = packet_title;
        field_title = new Vector<String>(20);
        packet_key = new Vector<String>(20);
    }
    public void setProp(String field,String key){
        field_title.addElement(field);
        packet_key.addElement(key);
    }
    public String getPacketTitle(){
        return packet_title;
    }
    public String[] getFieldTitle(){
        String[] title = new String[field_title.size()];
        field_title.copyInto(title);
        return title;
    }
    public String[] getPacketKey(){
        String[] key = new String[packet_key.size()];
        packet_key.copyInto(key);
        return key;
    }
}
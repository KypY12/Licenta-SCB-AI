package logger;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.List;
import java.util.Map;

public class LogConverter {

    private OOAICallback callback;

    public LogConverter(OOAICallback callback) {
        this.callback = callback;
    }


    public String getAsString(Object obj){

        if (obj instanceof java.lang.Exception){
            return getFromException((Exception)obj);
        }
        else if (obj instanceof AIFloat3){
            return getFromAIFloat3((AIFloat3)obj);
        }
        else if(obj instanceof java.util.ArrayList || obj instanceof java.util.LinkedList){
            return getFromList((List)obj);
        }
        else if(obj instanceof java.util.Map){
            return getFromMap((Map)obj);
        }

        return obj.toString();
    }


    private String getFromMap(Map map){
        StringBuffer buffer = new StringBuffer();
        for (Object mapKey : map.keySet()){
            buffer.append(mapKey + " : " + map.get(mapKey) + "\n");
        }
        return buffer.toString();
    }


    private String getFromException(Exception e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }


    private String getFromAIFloat3(AIFloat3 obj){
        return (new Float(obj.x).toString()) + " " + (new Float(obj.y).toString()) + " " + (new Float(obj.z).toString());
    }


    private String getFromList(List obj){
        StringBuffer buf = new StringBuffer();

        for (int index = 0; index < obj.size(); index++){
            buf.append("\n");
            buf.append(obj.get(index).toString() + ", ");
        }

        return buf.toString();
    }


}

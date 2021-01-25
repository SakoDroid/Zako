package Server.Utils.JSON;

import java.util.List;
import java.util.Map;

public class JSONDocument {

    private List content1 = null;
    private Map content2 = null;

    /**
     * Creates a JSONDocument from given ArrayList.
     *
     * @param data List that contains the data.
     */
    public JSONDocument(List data){
        this.content1 = data;
    }

    /**
     * Creates a JSONDocument from given Map.
     *
     * @param data Map that contains the data.
     */
    public JSONDocument(Map data){
        this.content2 = data;
    }

    /**
     * This method converts the JSONDocument into java usable.
     * According to the JSON data, you can cast the output of this method into Map or List.
     *
     * @<code> ArrayList al = (ArrayList) jd.toJava();
     * HashMap hm = (HashMap) jd.toJava();</code>
     *
     * @return An Object that is either a Map or List.
     */
    public Object toJava(){
        if (content1 != null)
            return this.content1;
        else
            return this.content2;
    }

    @Override
    public String toString(){
        return new ToString().getString();
    }

    private class ToString{

        public String getString(){
            if (content1 != null)
                return parseList(content1);
            else
                return parseMap(content2);
        }

        //Parses a List into String.
        private String parseList(List list){
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (Object obj : list){
                sb.append(this.objectToString(obj))
                .append(",\n");
            }
            if (sb.length() > 3)
                return sb.substring(0,sb.length()-2) + "\n]";
            else
                return sb.toString() + "\n}";
        }

        //Parses a Map into String.
        private String parseMap(Map map){
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            for (Object key : map.keySet()){
                sb.append(this.objectToString(key))
                .append(" : ")
                .append(this.objectToString(map.get(key)))
                .append(",\n");
            }
            if (sb.length() > 3)
                return sb.substring(0,sb.length()-2) + "\n}";
            else
                return sb.toString() + "\n}";
        }

        //Evaluates an object and turns it into String.
        private String objectToString(Object obj){
            StringBuilder sb = new StringBuilder();
            if (obj instanceof String)
                sb.append("\"").append(obj).append("\"");
            else if (obj instanceof Map)
                sb.append(this.parseMap((Map) obj));
            else if (obj instanceof List)
                sb.append(this.parseList((List) obj));
            else
                sb.append(obj);
            return sb.toString();
        }
    }

}

package pe.kr.ddakker.monitor.agent;

import java.lang.instrument.Instrumentation;
 
import pe.kr.ddakker.monitor.agent.transformer.TomcatTransformer;
 
public class MonitorAgent {

    public static void main(String[] args) {

        new TomcatTransformer();
    }

    public static void premain(String args, Instrumentation inst) throws Exception {
        System.out.println("A@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@A premain");
        inst.addTransformer(new TomcatTransformer());
    }
 
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        System.out.println("A@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@A agentmain");
        premain(args, inst);
    }
}

package pe.kr.ddakker.monitor.agent.transformer;

import javassist.*;
import pe.kr.ddakker.monitor.agent.MonitorAgent;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

public class Test {
    public static void main(String[] args) throws NotFoundException, IOException, CannotCompileException {
        TT test = new TT();
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get("pe.kr.ddakker.monitor.agent.transformer.TT");


        CtMethod[] mm = cc.getDeclaredMethods();

        Arrays.stream(mm).filter((CtMethod m) -> m.getName().equals("print"));

        for(int i=0; i<mm.length; i++){
            CtMethod cm = mm[i];

            final StringBuffer buffer = new StringBuffer();
            String name = cm.getName();
            System.out.println(name);



            cm.insertBefore("{ System.out.println(\"Hello11.say():\"); }");

            cc.writeFile("build");
            cc.toClass();

            TT h2 = new TT();
            h2.print();
        }

    }
}
